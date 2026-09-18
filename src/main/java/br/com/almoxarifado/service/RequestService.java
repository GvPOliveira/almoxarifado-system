package br.com.almoxarifado.service;

import br.com.almoxarifado.exception.ProductNotFoundInRequestException;
import br.com.almoxarifado.jdbc.BranchProductRepository;
import br.com.almoxarifado.jdbc.DatabaseConnection;
import br.com.almoxarifado.jdbc.RequestRepository;
import br.com.almoxarifado.model.*;

import java.sql.Connection;
import java.sql.SQLException;

public class RequestService {


    private BranchProductRepository branchProductRepository;
    private RequestRepository requestRepository;

    public RequestService(BranchProductRepository branchProductRepository, RequestRepository requestRepository) {
        this.branchProductRepository = branchProductRepository;
        this.requestRepository = requestRepository;
    }

    public void attendedProduct(Request request, String code, int quantityAttended) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection c = databaseConnection.connect()) {

            try {
                c.setAutoCommit(false);
                ProductRequest productRequest = request.findProductRequest(code);
                if (productRequest == null) {
                    throw new ProductNotFoundInRequestException();
                }
                productRequest.validateCanBeProcessed(quantityAttended);
                Movement movement = new Movement(quantityAttended, MovementType.OUTPUT, OriginType.REQUEST
                        , request.getNumberRequest());
                branchProductRepository.removeQuantity(productRequest.getBranchProduct(), movement, c);
                c.commit();
                productRequest.completeProcessing(quantityAttended);

            } catch (RuntimeException errorTransaction) {
                try {
                    c.rollback();
                } catch (SQLException errorRollback) {
                    throw new RuntimeException(errorRollback);
                }
                throw errorTransaction;
            }
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }
    }


    public void save(Request request) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection connection = databaseConnection.connect()) {
            connection.setAutoCommit(false);
            try {
                requestRepository.save(request, connection);
                connection.commit();
            } catch (RuntimeException errorTransaction) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    throw new RuntimeException(rollbackError);
                }
                throw errorTransaction;
            }
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }
    }


    public void reversalProduct(Request request, String code) {
        ProductRequest productRequest = request.findProductRequest(code);
        if (productRequest == null) {
            throw new ProductNotFoundInRequestException();
        }
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection connection = databaseConnection.connect()) {
            connection.setAutoCommit(false);
            int attendedQuantity = productRequest.getAttendedQuantity();
            productRequest.validateCanBeReverted();
            Movement movement = new Movement(attendedQuantity, MovementType.REVERSAL,
                    OriginType.REQUEST, request.getNumberRequest());
            try {
                branchProductRepository.addQuantity(productRequest.getBranchProduct(),
                        movement, connection);
                connection.commit();
                productRequest.completeReversal();

            } catch (RuntimeException errorTransaction) {
                try {
                    connection.rollback();
                } catch (SQLException errorRollback) {
                    throw new RuntimeException(errorRollback);
                }
                throw errorTransaction;
            }
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }


    }


}
