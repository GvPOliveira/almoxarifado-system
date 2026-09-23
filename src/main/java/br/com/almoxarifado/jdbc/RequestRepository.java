package br.com.almoxarifado.jdbc;

import br.com.almoxarifado.exception.ProductNotFoundInRequestException;
import br.com.almoxarifado.exception.ProductRequestNotFoundException;
import br.com.almoxarifado.exception.RequestNotFoundInBranch;
import br.com.almoxarifado.model.*;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class RequestRepository {


    public void save(Request request, Connection connection) {
        String sqlRequest = """
                INSERT INTO request(number_request, branch_id)
                VALUES(?, ?);
                """;
        String sqlProductRequest = """
                INSERT INTO product_request (request_id, branch_product_id, requested_quantity, attended_quantity)
                VALUES(?, ?, ?, ?);
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement preparedStatement2 = connection.prepareStatement(sqlProductRequest)) {
            preparedStatement.setString(1, request.getNumberRequest());
            preparedStatement.setInt(2, request.getBranch().getId());
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (!resultSet.next()) {
                    throw new SQLException("Não foi possivel recuperar o ID da request.");
                }
                int idRequest = resultSet.getInt(1);

                for (ProductRequest productRequest : request.getProductRequestMap().values()) {
                    int idBranchProduct, requestedQuantity, attendedQuantity;
                    idBranchProduct = productRequest.getBranchProduct().getId();
                    requestedQuantity = productRequest.getRequestedQuantity();
                    attendedQuantity = productRequest.getAttendedQuantity();

                    preparedStatement2.setInt(1, idRequest);
                    preparedStatement2.setInt(2, idBranchProduct);
                    preparedStatement2.setInt(3, requestedQuantity);
                    preparedStatement2.setInt(4, attendedQuantity);
                    preparedStatement2.executeUpdate();
                }
            }
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }
    }


    public void updateAttendance(int idProductRequest, int attendedQuantity, Connection connection) {
        String sql = """
                UPDATE product_request
                SET attended_quantity = ?,
                    processed = true
                WHERE id_product_request = ?;
                """;
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, attendedQuantity);
                preparedStatement.setInt(2, idProductRequest);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new ProductNotFoundInRequestException();
                }
            }
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }
    }

    public void updateReversed(int idProductRequest, Connection connection) {
        String sql = """
                UPDATE product_request
                SET reversed = true
                WHERE id_product_request = ?;
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idProductRequest);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new ProductRequestNotFoundException();
            }
        } catch (SQLException errorTransaction) {
            throw new RuntimeException(errorTransaction);
        }
    }


    public Request findRequestBy(String numberRequest, int idBranch, Connection connection) {
        String sql = """
                   SELECT
                            p.id_product,
                            p.code as product_code,
                            p.name as product_name,
                            b.id_branch,
                            b.code as branch_code,
                            b.name as branch_name,
                            r.number_request,
                            pr.id_product_request,
                            pr.attended_quantity,
                            pr.requested_quantity,
                            pr.processed,
                            pr.reversed,
                            bp.id_branchProduct,
                            bp.quantity,
                            bp.location
                            FROM request r
                            INNER JOIN branch b
                            ON r.branch_id = b.id_branch
                            INNER JOIN product_request pr
                            ON r.id_request = pr.request_id
                            INNER JOIN branch_product bp
                            ON pr.branch_product_id = bp.id_branchProduct
                            INNER JOIN product p
                            ON bp.product_id = p.id_product
                            WHERE r.number_request = ?
                            AND r.branch_id = ?;
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, numberRequest);
            preparedStatement.setInt(2, idBranch);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new RequestNotFoundInBranch();
                }

                Product product = productReconstructor(resultSet);
                Branch branch = branchReconstructor(resultSet);

                int bpId = resultSet.getInt("id_branchProduct");
                int bpQuantity = resultSet.getInt("quantity");
                String bpLocation = resultSet.getString("location");

                BranchProduct branchProduct = BranchProduct.reconstructor(bpId, product, branch,
                        bpQuantity, bpLocation);

                Map<String, ProductRequest> productRequestMap = new HashMap<>();

                int idProductRequest = resultSet.getInt("id_product_request");
                int attendedQuantity = resultSet.getInt("attended_quantity");
                int requestedQuantity = resultSet.getInt("requested_quantity");
                boolean processed = resultSet.getBoolean("processed");
                boolean reversed = resultSet.getBoolean("reversed");

                ProductRequest productRequest = ProductRequest.productRequestReconstructor(branchProduct,
                        requestedQuantity, attendedQuantity, reversed, processed, idProductRequest);
                productRequestMap.put(productRequest.getBranchProduct().getProduct().getCode(),
                        productRequest);
                while (resultSet.next()) {

                    product = productReconstructor(resultSet);

                    bpId = resultSet.getInt("id_branchProduct");
                    bpQuantity = resultSet.getInt("quantity");
                    bpLocation = resultSet.getString("location");


                    branchProduct = BranchProduct.reconstructor(bpId, product, branch,
                            bpQuantity, bpLocation);

                    idProductRequest = resultSet.getInt("id_product_request");
                    attendedQuantity = resultSet.getInt("attended_quantity");
                    requestedQuantity = resultSet.getInt("requested_quantity");
                    processed = resultSet.getBoolean("processed");
                    reversed = resultSet.getBoolean("reversed");
                    productRequest = ProductRequest.productRequestReconstructor(branchProduct,
                            requestedQuantity, attendedQuantity, reversed, processed, idProductRequest);
                    productRequestMap.put(productRequest.getBranchProduct().getProduct().getCode(),
                            productRequest);
                }
                Request request = Request.requestReconstructor(numberRequest, branch, productRequestMap);
                return request;

            }
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }
    }


    private Product productReconstructor(ResultSet resultSet) throws SQLException {
        int idProduct = resultSet.getInt("id_product");
        String codeProduct = resultSet.getString("product_code");
        String nameProduct = resultSet.getString("product_name");
        Product product = new Product(codeProduct, nameProduct);
        product.assignId(idProduct);
        return product;
    }

    private Branch branchReconstructor(ResultSet resultSet) throws SQLException {
        int idBranch = resultSet.getInt("id_branch");
        String codeBranch = resultSet.getString("branch_code");
        String nameBranch = resultSet.getString("branch_name");
        Branch branch = new Branch(codeBranch, nameBranch);
        branch.assignId(idBranch);
        return branch;
    }


}
