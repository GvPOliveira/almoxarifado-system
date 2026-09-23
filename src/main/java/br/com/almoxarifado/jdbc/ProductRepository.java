package br.com.almoxarifado.jdbc;

import br.com.almoxarifado.exception.ProductNotFoundException;
import br.com.almoxarifado.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {


    public Product save(Product product) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String code, name;
        code = product.getCode();
        name = product.getDescription();
        String sql = "INSERT INTO product(code, name) VALUES(?, ?)";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                int id = resultSet.getInt(1);
                product.assignId(id);
                return product;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public Product findById(int id) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "SELECT id_product, code, name FROM product WHERE id_product = ?";
        try (Connection c = databaseConnection.connect(); PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next() == false) {
                    throw new ProductNotFoundException();
                }
                Product product = mapProduct(resultSet);
                return product;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public Product findByCode(String code) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "SELECT id_product, code, name FROM product WHERE code = ?";
        try (Connection c = databaseConnection.connect(); PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setString(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next() == false) {
                    throw new ProductNotFoundException();
                }
                Product product = mapProduct(resultSet);
                return product;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public List<Product> findAllProducts() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "SELECT id_product, code, name FROM product";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Product> productList = new ArrayList<Product>();
                while (resultSet.next()) {
                    Product product = mapProduct(resultSet);
                    productList.add(product);
                }
                return productList;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }


    public void updateProductById(int id, String newName) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "UPDATE product SET name = ? WHERE id_product = ?";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, id);
            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new ProductNotFoundException();
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public void updateProductByCode(String code, String newName) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "UPDATE product SET name = ? WHERE code = ?";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, code);
            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new ProductNotFoundException();
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }


    public void deleteProductById(int id) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "DELETE FROM product WHERE id_product = ?";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new ProductNotFoundException();
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }


    public int deleteProductByCode(String code) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "DELETE FROM product WHERE code = ?";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setString(1, code);
            int result = preparedStatement.executeUpdate();
            return result;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }


    public Product mapProduct(ResultSet resultSet) throws SQLException {
        String name, code;
        int id;
        id = resultSet.getInt("id_product");
        code = resultSet.getString("code");
        name = resultSet.getString("name");
        Product product = new Product(code, name);
        product.assignId(id);
        return product;
    }
}
