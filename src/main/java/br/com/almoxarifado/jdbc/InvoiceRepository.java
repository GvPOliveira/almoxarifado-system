package br.com.almoxarifado.jdbc;

import br.com.almoxarifado.exception.InvoiceNotFoundException;
import br.com.almoxarifado.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRepository {

    public Invoice findByNumber(String numberInvoice, Connection connection) {
        String sql = """
                SELECT id_invoice, i.number_invoice, i.date_invoice, i.processed, i.branch_id, i.reversed,
                			b.code as branch_code, b.name as branch_name,
                            pi.id_product_invoice, pi.product_id, pi.quantity, pi.destination,
                            p.id_product, p.code as product_code, p.name as product_name
                FROM INVOICE i
                INNER JOIN branch b ON i.branch_id = b.id_branch
                INNER JOIN product_invoice pi ON pi.invoice_id = i.id_invoice
                INNER JOIN product p ON pi.product_id = p.id_product
                WHERE i.number_invoice = ?;
                """;
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, numberInvoice);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    throw new InvoiceNotFoundException();
                }

                int idInvoice, branchId, productInvoiceId, quantity, productId;
                LocalDateTime dateTime;
                String invoiceNumber, branchName, branchCode, productCode, productName, destinationStr;
                boolean processed, reversed;
                Destination destination;

                // INVOICE
                idInvoice = resultSet.getInt("id_invoice");
                invoiceNumber = resultSet.getString("number_invoice");
                dateTime = resultSet.getObject("date_invoice", LocalDateTime.class);
                processed = resultSet.getBoolean("processed");
                reversed = resultSet.getBoolean("reversed");

                //BRANCH
                branchId = resultSet.getInt("branch_id");
                branchCode = resultSet.getString("branch_code");
                branchName = resultSet.getString("branch_name");
                Branch branch = new Branch(branchCode, branchName);
                branch.assignId(branchId);

                productId = resultSet.getInt("id_product");
                productCode = resultSet.getString("product_code");
                productName = resultSet.getString("product_name");
                Product product = new Product(productCode, productName);
                product.assignId(productId);

                productInvoiceId = resultSet.getInt("id_product_invoice");
                destinationStr = resultSet.getString("destination");
                destination = Destination.valueOf(destinationStr);
                quantity = resultSet.getInt("quantity");

                ProductInvoice productInvoice = ProductInvoice.reconstructorProductInvoice(productInvoiceId, product, quantity, destination);
                List<ProductInvoice> productInvoiceList = new ArrayList<ProductInvoice>();
                productInvoiceList.add(productInvoice);

                while (resultSet.next()) {
                    productId = resultSet.getInt("id_product");
                    productCode = resultSet.getString("product_code");
                    productName = resultSet.getString("product_name");
                    product = new Product(productCode, productName);
                    product.assignId(productId);

                    productInvoiceId = resultSet.getInt("id_product_invoice");
                    destinationStr = resultSet.getString("destination");
                    destination = Destination.valueOf(destinationStr);
                    quantity = resultSet.getInt("quantity");
                    productInvoice = ProductInvoice.reconstructorProductInvoice(productInvoiceId, product, quantity, destination);
                    productInvoiceList.add(productInvoice);
                }
                return Invoice.reconstructorInvoice(idInvoice, invoiceNumber, branch, dateTime,
                        productInvoiceList, processed, reversed);

            }
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }
    }


    public void save(Invoice invoice, Connection connection) {
        String sqlInvoice = """
                INSERT INTO invoice(number_invoice, date_invoice, processed,branch_id,reversed)
                VALUES(?, ?, ?, ?, ?);
                """;
        String sqlProductInvoice = """
                INSERT INTO product_invoice (invoice_id, product_id, quantity, destination)
                VALUES(?, ?, ?, ?);
                """;
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInvoice, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement preparedStatement2 = connection.prepareStatement(sqlProductInvoice)) {

                preparedStatement.setString(1, invoice.getNumberInvoice());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(invoice.getDate()));
                preparedStatement.setBoolean(3, invoice.isProcessed());
                preparedStatement.setInt(4, invoice.getBranchDestination().getId());
                preparedStatement.setBoolean(5, invoice.isReversed());
                preparedStatement.executeUpdate();

                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (!resultSet.next()) {
                        throw new SQLException("Não foi possivel recuperar o ID da invoice");
                    }
                    int idInvoice = resultSet.getInt(1);

                    for (ProductInvoice productInvoice : invoice.getProductInvoiceView()) {
                        preparedStatement2.setInt(1, idInvoice);
                        preparedStatement2.setInt(2, productInvoice.getProduct().getId());
                        preparedStatement2.setInt(3, productInvoice.getQuantity());
                        preparedStatement2.setString(4, productInvoice.getDestination().name());
                        preparedStatement2.executeUpdate();
                    }
                }
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }


}
