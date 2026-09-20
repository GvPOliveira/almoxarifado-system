package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.exception.*;

public class ProductRequest {
    private int id;
    private int idProductRequest;
    private Request request;
    private BranchProduct branchProduct;
    private int requestedQuantity;
    private int attendedQuantity;
    private boolean reversed, processed;

    public int getIdProductRequest() {
        return idProductRequest;
    }

    public ProductRequest(Request request, BranchProduct branchProduct, int requestedQuantity) {
        if (requestedQuantity <= 0) {
            throw new InvalidQuantityException();
        }
        this.idProductRequest = 0;
        this.request = request;
        this.branchProduct = branchProduct;
        this.requestedQuantity = requestedQuantity;
        attendedQuantity = 0;
        reversed = false;
        processed = false;
    }

    private ProductRequest(BranchProduct branchProduct, int requestedQuantity, int attendedQuantity,
                           boolean reversed, boolean processed, int idProductRequest) {
        this.idProductRequest = idProductRequest;
        this.branchProduct = branchProduct;
        this.requestedQuantity = requestedQuantity;
        this.attendedQuantity = attendedQuantity;
        this.reversed = reversed;
        this.processed = processed;
    }

    public static ProductRequest productRequestReconstructor(BranchProduct branchProduct, int requestedQuantity, int attendedQuantity,
                                                             boolean reversed, boolean processed, int idProductRequest) {
        return new ProductRequest(branchProduct, requestedQuantity, attendedQuantity, reversed, processed, idProductRequest);
    }



    public void validateCanBeProcessed(int attendedQuantity) {
        if (processed) {
            throw new ProductRequestAlreadyProcessedException();
        }
        if (attendedQuantity < 0) {
            throw new InvalidQuantityException();
        }
        if (attendedQuantity > requestedQuantity) {
            throw new RequestedQuantityExceededException();
        }
    }

    public void completeProcessing(int quantity) {
        this.attendedQuantity = quantity;
        processed = true;
    }

    public void defineAttendedQuantity(int attendedQuantity) {
        this.attendedQuantity = attendedQuantity;
    }


    public void addRequestQuantity(int requestedQuantity) {
        if (processed) {
            throw new ProductRequestAlreadyProcessedException();
        }
        if (requestedQuantity < 0) {
            throw new InvalidQuantityException();
        }
        this.requestedQuantity += requestedQuantity;
    }

    public void validateCanBeReverted() {
        if(!processed){
            throw new ProductRequestNotProcessedException();
        }
        if (reversed) {
            throw new ProductRequestAlreadyRevertedException();
        }
        if (attendedQuantity == 0) {
            throw new NoReversionException();
        }
    }

    public void completeReversal() {
        this.reversed = true;
    }

    public BranchProduct getBranchProduct() {
        return branchProduct;
    }

    public boolean isReversed() {
        return reversed;
    }

    public boolean isProcessed() {
        return processed;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAttendedQuantity() {
        return attendedQuantity;
    }
}
