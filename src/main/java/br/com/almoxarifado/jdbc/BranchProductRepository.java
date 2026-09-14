package br.com.almoxarifado.jdbc;

import br.com.almoxarifado.exception.BranchProductNotFoundException;
import br.com.almoxarifado.exception.InsufficientStockException;
import br.com.almoxarifado.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BranchProductRepository {


    public BranchProduct findBranchProduct(int id) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = """
                SELECT
                	p.id_product,
                    p.code as product_code,
                    p.name as product_name,
                    b.id_branch,
                    b.code as branch_code,
                    b.name as branch_name,
                    m.id_movement,
                    m.date,
                    m.movementType,
                	m.originType,
                    m.originNumber,
                    m.quantity as move_quantity,
                    bp.id_branchProduct,
                	bp.quantity as bp_quantity,
                	bp.location
                FROM branch_product bp
                LEFT JOIN product p
                    ON bp.product_id = p.id_product
                LEFT JOIN branch b
                    ON bp.branch_id = b.id_branch
                LEFT JOIN movement m
                    ON bp.id_branchProduct = m.fk_branchProduct
                WHERE bp.id_branchProduct = ?
                ORDER BY m.date ASC;
                """;
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                String productCode, productDescription, branchCode, branchName,
                        branchProductLocation, movementType, movementOriginType, movementOriginNumber, movementUuid;

                int productId, branchId, branchProductId, branchProductQuantity, movementQuantity;

                if (resultSet.next() == false) {
                    throw new BranchProductNotFoundException();
                }

                productId = resultSet.getInt("id_product");
                productCode = resultSet.getString("product_code");
                productDescription = resultSet.getString("product_name");

                Product product = new Product(
                        productCode,
                        productDescription);
                product.assignId(productId);

                branchId = resultSet.getInt("id_branch");
                branchCode = resultSet.getString("branch_code");
                branchName = resultSet.getString("branch_name");

                Branch branch = new Branch(
                        branchCode,
                        branchName);
                branch.assignId(branchId);

                branchProductId = resultSet.getInt("id_branchProduct");
                branchProductQuantity = resultSet.getInt("bp_quantity");
                branchProductLocation = resultSet.getString("location");

                movementType = resultSet.getString("movementType");
                LocalDateTime m_date = resultSet.getObject("date", LocalDateTime.class);
                movementOriginType = resultSet.getString("originType");
                movementOriginNumber = resultSet.getString("originNumber");
                movementQuantity = resultSet.getInt("move_quantity");
                movementUuid = resultSet.getString("id_movement");
                List<Movement> movementList = new ArrayList<Movement>();

                if (movementUuid != null) {
                    UUID uuid = UUID.fromString(movementUuid);

                    Movement movement = Movement.reconstructMovement(
                            uuid, m_date,
                            movementQuantity,
                            MovementType.valueOf(movementType),
                            OriginType.valueOf(movementOriginType),
                            movementOriginNumber);
                    movementList.add(movement);

                    while (resultSet.next()) {
                        movementUuid = resultSet.getString("id_movement");
                        if (movementUuid == null) {
                            break;
                        }
                        uuid = UUID.fromString(movementUuid);
                        m_date = resultSet.getObject("date", LocalDateTime.class);
                        movementQuantity = resultSet.getInt("move_quantity");
                        movementType = resultSet.getString("movementType");
                        movementOriginType = resultSet.getString("originType");
                        movementOriginNumber = resultSet.getString("originNumber");
                        movement = Movement.reconstructMovement(
                                uuid,
                                m_date,
                                movementQuantity,
                                MovementType.valueOf(movementType),
                                OriginType.valueOf(movementOriginType),
                                movementOriginNumber);
                        movementList.add(movement);
                    }
                }
                //Reconstrução do branchProduct após reconstrução dos objetos e lista relacionado
                BranchProduct branchProduct = BranchProduct.reconstructor(
                        branchProductId,
                        product,
                        branch,
                        branchProductQuantity,
                        branchProductLocation,
                        movementList);
                return branchProduct;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }


    public BranchProduct findBranchProduct(int productId, int branchId) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = """
                SELECT bp.id_branchProduct, bp.quantity, bp.location, 
                       p.id_product, p.code as product_code, p.name as product_name,
                       b.id_branch, b.code as branch_code,b.name as branch_name
                FROM branch_product bp
                INNER JOIN product p 
                ON p.id_product = bp.product_id
                INNER JOIN branch b 
                ON b.id_branch = bp.branch_id
                WHERE bp.product_id = ?
                AND bp.branch_id = ?;
                """;
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, branchId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                int id_bp, quantity;
                String location;

                Product product = new Product(resultSet.getString("product_code"),
                        resultSet.getString("product_name"));
                product.assignId(resultSet.getInt("id_product"));

                Branch branch = new Branch(resultSet.getString("branch_code"),
                        resultSet.getString("branch_name"));
                branch.assignId(resultSet.getInt("id_branch"));

                id_bp = resultSet.getInt("id_branchProduct");
                quantity = resultSet.getInt("quantity");
                location = resultSet.getString("location");

                BranchProduct branchProduct = BranchProduct.reconstructor(id_bp, product, branch, quantity, location);

                return branchProduct;
            }
        } catch (SQLException connectionError) {
            throw new RuntimeException(connectionError);
        }


    }


    public void addQuantity(BranchProduct branchProduct, Movement movement, Connection connection) {
        String update = "UPDATE branch_product SET quantity = quantity + ? Where id_branchProduct = ?";
        String insert = """
                INSERT INTO movement(id_movement, originType, originNumber, movementType, date, quantity, fk_branchProduct)
                VALUES(?, ?, ?, ?, ?, ?, ?);
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(update);
             PreparedStatement ps = connection.prepareStatement(insert)) {
            preparedStatement.setInt(2, branchProduct.getId());

            preparedStatement.setInt(1, movement.getQuantity());
            int updateRows = preparedStatement.executeUpdate();
            if (updateRows == 0) {
                throw new BranchProductNotFoundException();
            }

            String uuid = movement.getUuid().toString();
            ps.setString(1, uuid);
            ps.setString(2, movement.getOriginType().toString());
            ps.setString(3, movement.getOriginNumber());
            ps.setString(4, movement.getMovementType().toString());
            ps.setTimestamp(5, Timestamp.valueOf(movement.getDate()));
            ps.setInt(6, movement.getQuantity());
            ps.setInt(7, branchProduct.getId());
            ps.executeUpdate();
        } catch (SQLException errorAddQuantity) {
            throw new RuntimeException(errorAddQuantity);
        }
    }


    public void removeQuantity(BranchProduct branchProduct, Movement movement, Connection connection) {
        String update = """
                UPDATE branch_product
                SET quantity = quantity - ?
                WHERE id_branchProduct = ?
                AND quantity >= ?;
                """;
        String insert = """
                INSERT INTO movement(id_movement, originType, originNumber,
                 movementType, date, quantity, fk_branchProduct)
                VALUES(?, ?, ?, ?, ?, ?, ?);
                """;
        String select = "SELECT quantity FROM branch_product WHERE id_branchProduct = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insert);
             PreparedStatement ps = connection.prepareStatement(update);
             PreparedStatement ps2 = connection.prepareStatement(select)) {

            ps.setInt(1, movement.getQuantity());
            ps.setInt(2, branchProduct.getId());
            ps.setInt(3, movement.getQuantity());
            int updateRowsAffeted = ps.executeUpdate();
            if (updateRowsAffeted == 0) {
                ps2.setInt(1, branchProduct.getId());
                try (ResultSet rowsFound = ps2.executeQuery()) {
                    if (!rowsFound.next()) {
                        throw new BranchProductNotFoundException();
                    } else {
                        throw new InsufficientStockException("Quantidade superior a quantidade disponivel no estoque.");
                    }
                }
            }
            String uuid = movement.getUuid().toString();
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, movement.getOriginType().toString());
            preparedStatement.setString(3, movement.getOriginNumber());
            preparedStatement.setString(4, movement.getMovementType().toString());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(movement.getDate()));
            preparedStatement.setInt(6, movement.getQuantity());
            preparedStatement.setInt(7, branchProduct.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException operationError) {
            throw new RuntimeException(operationError);
        }
    }

    public void save(BranchProduct branchProduct, Movement movement, Connection c) {
        String insertBranchProduct = """
                INSERT INTO branch_product(quantity, location,
                product_id, branch_id)
                VALUES(?, ?, ?, ?);
                """;
        String insertMovement = """
                INSERT INTO movement(id_movement, originType, originNumber,
                 movementType, date, quantity, fk_branchProduct)
                VALUES(?, ?, ?, ?, ?, ?, ?);
                """;
        try (PreparedStatement ps = c.prepareStatement(insertBranchProduct, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement ps1 = c.prepareStatement(insertMovement)) {

            ps.setInt(1, movement.getQuantity());
            ps.setString(2, branchProduct.getLocation());
            ps.setInt(3, branchProduct.getProduct().getId());
            ps.setInt(4, branchProduct.getBranch().getId());
            ps.executeUpdate();
            try (ResultSet generatedKey = ps.getGeneratedKeys()) {
                generatedKey.next();
                int idBranchproduct = generatedKey.getInt(1);

                ps1.setString(1, movement.getUuid().toString());
                ps1.setString(2, movement.getOriginType().toString());
                ps1.setString(3, movement.getOriginNumber());
                ps1.setString(4, movement.getMovementType().toString());
                ps1.setTimestamp(5, Timestamp.valueOf(movement.getDate()));
                ps1.setInt(6, movement.getQuantity());
                ps1.setInt(7, idBranchproduct);
                ps1.executeUpdate();
            }
        } catch (SQLException transactionError) {
            throw new RuntimeException(transactionError);
        }
    }


}
