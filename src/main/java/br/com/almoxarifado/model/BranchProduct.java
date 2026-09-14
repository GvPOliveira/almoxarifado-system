package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.exception.InsufficientStockException;
import br.com.almoxarifado.exception.InvalidQuantityException;
import br.com.almoxarifado.exception.MovementNotFoundException;
import br.com.almoxarifado.exception.ProductReversalProcessedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BranchProduct {
    private Product product;
    private Branch branch;
    private int id = 0;
    private int quantity;
    private String location;
    private List<Movement> movementList;
    private List<Movement> movementView;

    public BranchProduct() {
    }

    public int getId() {
        return id;
    }

    public BranchProduct(Product product, Branch branch, int quantity, OriginType originType, String originNumber) {
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        this.product = product;
        this.branch = branch;
        this.quantity = quantity;
        this.location = null;
        movementList = new ArrayList<>();
        movementView = Collections.unmodifiableList(movementList);
        registerMovement(quantity, MovementType.ENTRY, originType, originNumber);
    }

    private BranchProduct(int id, Product product, Branch branch, int quantity, String location, List<Movement> movementList) {
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        this.id = id;
        this.product = product;
        this.branch = branch;
        this.quantity = quantity;
        this.location = location;
        this.movementList = movementList;
        movementView = Collections.unmodifiableList(movementList);
    }

    private BranchProduct(int id_bp, Product product, Branch branch, int quantity, String location) {
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        this.id = id_bp;
        this.product = product;
        this.branch = branch;
        this.quantity = quantity;
        this.location = location;
        this.movementList = new ArrayList<>();
        movementView = Collections.unmodifiableList(movementList);
    }

    public void assignId(int id) {
        if (id <= 0) {
            throw new InvalidProductIdException();
        }
        if (this.id != 0) {
            throw new BranchAlreadyExists();
        } else {
            this.id = id;
        }
    }

    public static BranchProduct reconstructor(int bp_id, Product product, Branch branch,
                                              int quantity, String location, List<Movement> movementList) {
        BranchProduct branchProduct = new BranchProduct(bp_id, product, branch, quantity, location, movementList);
        return branchProduct;
    }

    public static BranchProduct reconstructor(int bp_id, Product product, Branch branch,
                                              int quantity, String location) {
        BranchProduct branchProduct = new BranchProduct(bp_id, product, branch, quantity, location);
        return branchProduct;
    }


    public void addQuantity(int quantity, OriginType originType, String originNumber) {
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        this.quantity += quantity;
        registerMovement(quantity, MovementType.ENTRY, originType, originNumber);
    }

    public void removeQuantity(int quantity, OriginType originType, String originNumber) {
        if (quantity < 0) {
            throw new InvalidQuantityException();
        } else if (this.quantity < quantity) {
            throw new InsufficientStockException("Insufficient Stock. Available: " + this.quantity + ", requested: " + quantity);
        }
        this.quantity -= quantity;
        if (quantity > 0) {
            registerMovement(quantity, MovementType.OUTPUT, originType, originNumber);
        }
    }

    public void processReversal(int quantity, OriginType originType, String originNumber) {
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        findProductReversal(originType, originNumber);
        for (int i = 0; i < movementList.size(); i++) {
            if (movementList.get(i).getMovementType() == MovementType.ENTRY) {
                if (movementList.get(i).getOriginType() == originType && movementList.get(i).getOriginNumber().equals(originNumber)) {
                    this.quantity -= quantity;
                    registerMovement(quantity, MovementType.REVERSAL, originType, originNumber);
                    return;
                }
            }
            if (movementList.get(i).getMovementType() == MovementType.OUTPUT) {
                if (movementList.get(i).getOriginType() == originType && movementList.get(i).getOriginNumber().equals(originNumber)) {
                    this.quantity += quantity;
                    registerMovement(quantity, MovementType.REVERSAL, originType, originNumber);
                    return;
                }
            }
        }

        throw new MovementNotFoundException();
    }

    public void findProductReversal(OriginType originType, String originNumber) {
        for (int i = 0; i < movementList.size(); i++) {
            if (movementList.get(i).getMovementType() == MovementType.REVERSAL && movementList.get(i).getOriginType()
                    == originType && movementList.get(i).getOriginNumber().equals(originNumber)) {
                throw new ProductReversalProcessedException();
            }
        }

    }


    private void registerMovement(int quantity, MovementType type, OriginType originType, String originNumber) {
        Movement movement = new Movement(quantity, type, originType, originNumber);
        movementList.add(movement);

    }

    @Override
    public String toString() {
        return "BranchProduct{" +
                "product_id =" + getProduct().getId() + "\nCode: " + getProduct().getCode() +
                "\nName- " + getProduct().getDescription() +
                "\nbranch_id =" + getBranch().getId() + "\nCode: " + getBranch().getCode() +
                "\nName- " + getBranch().getName() +
                "\nquantity= " + getQuantity() +
                "\nlocation= " + getLocation() + '\'' +
                '}';
    }

    public Product getProduct() {
        return product;
    }

    public List<Movement> getMovementList() {
        return movementView;
    }

    public Branch getBranch() {
        return branch;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String Location) {
        this.location = Location;
    }
}
