package br.com.almoxarifado.service;

import br.com.almoxarifado.jdbc.BranchProductRepository;
import br.com.almoxarifado.jdbc.DatabaseConnection;
import br.com.almoxarifado.jdbc.InvoiceRepository;
import br.com.almoxarifado.model.*;

import java.sql.Connection;
import java.sql.SQLException;

public class InvoiceService {

    private BranchProductRepository branchProductRepository;
    private InvoiceRepository invoiceRepository;

    public InvoiceService(BranchProductRepository branchProductRepository, InvoiceRepository invoiceRepository) {
        this.branchProductRepository = branchProductRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice findByNumberInvoice(String numberInvoice) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection connection = databaseConnection.connect()) {
            return invoiceRepository.findByNumber(numberInvoice, connection);
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }
    }


    public void processInvoice(Invoice invoice) {
        invoice.validateCanBeProcessed();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection connection = databaseConnection.connect()) {
            connection.setAutoCommit(false);
            try {
                for (ProductInvoice productInvoice : invoice.getProductInvoiceView()) {
                    int idProduct = productInvoice.getProduct().getId();
                    int idBranch = invoice.getBranchDestination().getId();
                    int quantity = productInvoice.getQuantity();
                    String numberInvoice = invoice.getNumberInvoice();

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
                invoice.completeProcessing();
                invoiceRepository.save(invoice, connection);
                connection.commit();
            } catch (RuntimeException transactionError) {
                try {
                    connection.rollback();
                } catch (SQLException errorRollback) {
                    throw new RuntimeException(errorRollback);
                }
                throw transactionError;
            }
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }
    }


}
