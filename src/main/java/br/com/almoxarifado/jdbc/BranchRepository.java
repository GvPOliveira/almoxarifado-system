package br.com.almoxarifado.jdbc;

import br.com.almoxarifado.exception.BranchNotFoundException;
import br.com.almoxarifado.model.Branch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchRepository {


    public Branch save(Branch branch) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "INSERT INTO branch(code, name) VALUES(?, ?)";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, branch.getCode());
            preparedStatement.setString(2, branch.getName());
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                int id = resultSet.getInt(1);
                branch.assignId(id);
                return branch;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public Branch findById(int id) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "SELECT id_branch, code, name FROM branch WHERE id_branch = ?";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapBranch(resultSet);
                }
                throw new BranchNotFoundException();
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public Branch findByCode(String code) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "SELECT id_branch, code, name FROM branch WHERE code = ?";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setString(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapBranch(resultSet);
                }
                throw new BranchNotFoundException();
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public List<Branch> findByAll() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "SELECT id_branch, code, name FROM branch";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Branch> branchList = new ArrayList<>();
            while (resultSet.next()) {
                Branch branch = mapBranch(resultSet);
                branchList.add(branch);
            }
            return branchList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }


    public void updateNameByCode(String code, String newName) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String sql = "UPDATE branch SET name = ? WHERE code = ?";
        try (Connection c = databaseConnection.connect();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, code);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 0){
                throw new BranchNotFoundException();
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

    }

    public Branch mapBranch(ResultSet resultSet) throws SQLException {
        String code, name;
        int id;
        id = resultSet.getInt("id_branch");
        code = resultSet.getString("code");
        name = resultSet.getString("name");
        Branch branch = new Branch(code, name);
        branch.assignId(id);
        return branch;
    }


}
