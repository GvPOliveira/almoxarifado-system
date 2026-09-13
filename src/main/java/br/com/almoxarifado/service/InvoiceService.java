package br.com.almoxarifado.service;

import br.com.almoxarifado.jdbc.BranchProductRepository;
import br.com.almoxarifado.jdbc.DatabaseConnection;
import br.com.almoxarifado.model.*;

import java.sql.Connection;
import java.sql.SQLException;

public class InvoiceService {

    private BranchProductRepository branchProductRepository;


    public InvoiceService(BranchProductRepository branchProductRepository) {
        this.branchProductRepository = branchProductRepository;
    }


    public void processInvoice(Invoice invoice) {
        invoice.validateCanBeProcessed();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection connection = databaseConnection.connect()) {
            connection.setAutoCommit(false);
            try {
                for (int i = 0; i < invoice.getProductInvoiceView().size(); i++) {
                    int idProduct = invoice.getProductInvoiceView().get(i).getProduct().getId();
                    int idBranch = invoice.getBranchDestination().getId();
                    int quantity = invoice.getProductInvoiceView().get(i).getQuantity();
                    String numberInvoice = invoice.getNumberInvoice();
                    ProductInvoice productInvoice = invoice.getProductInvoiceView().get(i);

                    Destination destination = productInvoice.getDestination();
                    if (destination == Destination.STOCK) {
                        Movement movement = new Movement(productInvoice.getQuantity(),
                                MovementType.ENTRY, OriginType.INVOICE, numberInvoice);
                        BranchProduct branchProduct = branchProductRepository.findBranchProduct(idProduct, idBranch);
                        if (branchProduct != null) {
                            branchProductRepository.addQuantity(branchProduct, movement, connection);
                        } else {
                            BranchProduct newBranchProduct = new BranchProduct(productInvoice.getProduct(),
                                    invoice.getBranchDestination(), quantity,
                                    OriginType.INVOICE, numberInvoice);
                            branchProductRepository.save(newBranchProduct, movement, connection);
                        }
                    }
                }
            } catch (RuntimeException transactionError) {
                try {
                    connection.rollback();
                } catch (SQLException errorRollback) {
                    throw new RuntimeException(errorRollback);
                }
                throw new RuntimeException(transactionError);
            }
            connection.commit();
            invoice.completeProcessing();

        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }
    }


}
