package br.com.almoxarifado.service;

import br.com.almoxarifado.exception.ProductNotFoundInRequestException;
import br.com.almoxarifado.jdbc.BranchProductRepository;
import br.com.almoxarifado.jdbc.DatabaseConnection;
import br.com.almoxarifado.model.*;

import java.sql.Connection;
import java.sql.SQLException;

public class RequestService {


    private BranchProductRepository branchProductRepository;

    public RequestService(BranchProductRepository branchProductRepository) {
        this.branchProductRepository = branchProductRepository;
    }

        /*    branchProduct.removeQuantity(attendedQuantity,OriginType.REQUEST,request.getNumberRequest());
        this.attendedQuantity =attendedQuantity;
        this.processed =true;
*/

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


}
