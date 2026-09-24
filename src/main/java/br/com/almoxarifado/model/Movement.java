package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.InvalidQuantityException;

import java.util.UUID;
import java.time.LocalDateTime;

public class Movement {
    final UUID uuid;
    final MovementType movementType;
    final LocalDateTime date;
    final int quantity;
    final String originNumber;
    final OriginType originType;


    public Movement(int quantity, MovementType type, OriginType originType, String originNumber) {
        if (quantity < 0) {
            throw new InvalidQuantityException();
        }
        if (type == null || originType == null || originNumber == null) {
            throw new NullPointerException();
        }
        if (originNumber.isBlank()) {
            throw new IllegalArgumentException();
        }

        uuid = UUID.randomUUID();
        this.quantity = quantity;
        this.movementType = type;
        this.originType = originType;
        this.originNumber = originNumber;
        this.date = LocalDateTime.now();
    }

    private Movement(UUID uuid, LocalDateTime date, int quantity, MovementType type, OriginType originType, String originNumber) {
        if (quantity < 0) {
            throw new InvalidQuantityException();
        }
        if (type == null || originType == null || originNumber == null) {
            throw new NullPointerException();
        }
        if (originNumber.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.uuid = uuid;
        this.quantity = quantity;
        this.movementType = type;
        this.originType = originType;
        this.originNumber = originNumber;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Movement{" +
                "uuid='" + uuid.toString() + '\'' +
                ", quantity=" + quantity +
                ", movementType='" + movementType + '\'' +
                ", originType='" + originType + '\'' +
                ", originNumber='" + originNumber + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public static Movement reconstructMovement(UUID uuid, LocalDateTime date, int quantity, MovementType type, OriginType originType, String originNumber) {
        return new Movement(uuid, date, quantity, type, originType, originNumber);

    }

    public MovementType getMovementType() {
        return movementType;
    }

    public UUID getUuid() {
        return uuid;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getOriginNumber() {
        return originNumber;
    }

    public OriginType getOriginType() {
        return originType;
    }
}
