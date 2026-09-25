-- =========================================================================
-- SCRIPT DE CRIAÇÃO E POPULAÇÃO DO BANCO DE DADOS: ALMOXARIFADO
-- =========================================================================

-- 1. Criação do Banco de Dados
DROP DATABASE IF EXISTS almoxarifado;
CREATE DATABASE almoxarifado;
USE almoxarifado;

-- =========================================================================
-- 2. CRIAÇÃO DAS TABELAS
-- =========================================================================

CREATE TABLE product (
                         id_product INT PRIMARY KEY AUTO_INCREMENT,
                         code VARCHAR(20) UNIQUE NOT NULL,
                         name VARCHAR(100) NOT NULL
);

CREATE TABLE branch (
                        id_branch INT PRIMARY KEY AUTO_INCREMENT,
                        code VARCHAR(20) UNIQUE NOT NULL,
                        name VARCHAR(60) NOT NULL
);

CREATE TABLE branch_product (
                                id_branchProduct INT PRIMARY KEY AUTO_INCREMENT,
                                quantity INT NOT NULL,
                                location VARCHAR(80),
                                product_id INT NOT NULL,
                                branch_id INT NOT NULL,
                                CONSTRAINT fk_branchProduct_product
                                    FOREIGN KEY (product_id) REFERENCES product(id_product),
                                CONSTRAINT fk_branchProduct_branch
                                    FOREIGN KEY (branch_id) REFERENCES branch(id_branch),
                                CONSTRAINT uk_branchProduct
                                    UNIQUE(product_id, branch_id)
);

CREATE TABLE movement (
                          id_movement CHAR(36) PRIMARY KEY,
                          originType VARCHAR(20) NOT NULL,
                          originNumber VARCHAR(20) NOT NULL,
                          movementType VARCHAR(20) NOT NULL,
                          date DATETIME NOT NULL,
                          quantity INT NOT NULL,
                          fk_branchProduct INT NOT NULL,
                          CONSTRAINT fk_movement_branchProduct
                              FOREIGN KEY (fk_branchProduct) REFERENCES branch_product(id_branchProduct)
);

CREATE TABLE request (
                         id_request INT AUTO_INCREMENT PRIMARY KEY,
                         number_request VARCHAR(50) NOT NULL,
                         branch_id INT NOT NULL,
                         CONSTRAINT uk_request_number_branch
                             UNIQUE (number_request, branch_id),
                         CONSTRAINT fk_request_branch
                             FOREIGN KEY (branch_id) REFERENCES branch(id_branch)
);

CREATE TABLE product_request (
                                 id_product_request INT AUTO_INCREMENT PRIMARY KEY,
                                 request_id INT NOT NULL,
                                 branch_product_id INT NOT NULL,
                                 requested_quantity INT NOT NULL,
                                 attended_quantity INT NOT NULL DEFAULT 0,
                                 processed BOOLEAN NOT NULL DEFAULT FALSE,
                                 reversed BOOLEAN NOT NULL DEFAULT FALSE,
                                 CONSTRAINT fk_product_request_request
                                     FOREIGN KEY (request_id) REFERENCES request(id_request),
                                 CONSTRAINT fk_product_request_branch_product
                                     FOREIGN KEY (branch_product_id) REFERENCES branch_product(id_branchProduct)
);

CREATE TABLE invoice (
                         id_invoice INT PRIMARY KEY AUTO_INCREMENT,
                         number_invoice VARCHAR(50) NOT NULL,
                         date_invoice DATETIME NOT NULL,
                         processed BOOLEAN NOT NULL DEFAULT FALSE,
                         branch_id INT NOT NULL,
                         reversed BOOLEAN NOT NULL DEFAULT FALSE,
                         CONSTRAINT fk_invoice_branch
                             FOREIGN KEY (branch_id) REFERENCES branch(id_branch)
);

CREATE TABLE product_invoice (
                                 id_product_invoice INT PRIMARY KEY AUTO_INCREMENT,
                                 invoice_id INT NOT NULL,
                                 product_id INT NOT NULL,
                                 quantity INT NOT NULL,
                                 destination VARCHAR(50) NOT NULL,
                                 CONSTRAINT fk_product_invoice_invoice
                                     FOREIGN KEY (invoice_id) REFERENCES invoice(id_invoice),
                                 CONSTRAINT fk_product_invoice_product
                                     FOREIGN KEY (product_id) REFERENCES product(id_product)
);

-- =========================================================================
-- 3. INSERÇÃO DE DADOS INICIAIS
-- =========================================================================

-- -------------------------------------------------------------------------
-- Filiais
-- -------------------------------------------------------------------------

INSERT INTO branch (code, name) VALUES
                                    ('01', 'Filial Sul'),
                                    ('02', 'Filial Leste');

-- -------------------------------------------------------------------------
-- Produtos
-- -------------------------------------------------------------------------

INSERT INTO product (code, name) VALUES
                                     ('001', 'Caneta Azul'),
                                     ('002', 'Parafuso 1/2'),
                                     ('003', 'Porca 1/2');

-- -------------------------------------------------------------------------
-- Produtos por filial
-- -------------------------------------------------------------------------

INSERT INTO branch_product (quantity, location, product_id, branch_id) VALUES
                                                                           (130, 'Corredor A', 1, 1),
                                                                           (50, 'Corredor B', 2, 1);

-- =========================================================================
-- 4. MOVIMENTAÇÕES INICIAIS
-- =========================================================================

-- Produto 1:
-- Estoque inicial de 130 unidades.
-- A movimentação abaixo representa uma entrada de 130 unidades.

INSERT INTO movement (
    id_movement,
    originType,
    originNumber,
    movementType,
    date,
    quantity,
    fk_branchProduct
) VALUES (
             '2ce92b0b-e540-4951-988d-bc5c82055b8c',
             'INVOICE',
             '123',
             'ENTRY',
             '2026-09-20 10:00:00',
             130,
             1
         );

-- Produto 2:
-- Estoque atual de 50 unidades.
-- Histórico: entrada de 150 e saída de 100.

INSERT INTO movement (
    id_movement,
    originType,
    originNumber,
    movementType,
    date,
    quantity,
    fk_branchProduct
) VALUES
      (
          '6b267968-a25a-4fa2-ae1d-c2b7389a17fd',
          'INVOICE',
          '124',
          'ENTRY',
          '2026-09-20 11:00:00',
          150,
          2
      ),
      (
          '7d9f6b5a-1c42-4e83-91ab-3d6c8f52a714',
          'REQUEST',
          '122',
          'OUTPUT',
          '2026-09-20 14:30:00',
          100,
          2
      );

-- =========================================================================
-- 5. REQUISIÇÕES
-- =========================================================================

INSERT INTO request (number_request, branch_id) VALUES
                                                    ('700', 1),
                                                    ('701', 1),
                                                    ('702', 1);

-- -------------------------------------------------------------------------
-- Produtos das requisições
-- -------------------------------------------------------------------------

INSERT INTO product_request (
    request_id,
    branch_product_id,
    requested_quantity,
    attended_quantity,
    processed,
    reversed
) VALUES
      (1, 1, 300, 200, FALSE, FALSE),
      (2, 1, 150, 150, FALSE, FALSE),
      (3, 1, 50, 0, FALSE, FALSE);

-- =========================================================================
-- 6. NOTAS FISCAIS
-- =========================================================================

-- Invoice ainda não processada para permitir o teste de processamento.

INSERT INTO invoice (
    number_invoice,
    date_invoice,
    processed,
    branch_id,
    reversed
) VALUES (
             'NF-1001',
             '2026-09-21 09:00:00',
             FALSE,
             1,
             FALSE
         );

-- =========================================================================
-- 7. PRODUTOS DA NOTA FISCAL
-- =========================================================================

INSERT INTO product_invoice (
    invoice_id,
    product_id,
    quantity,
    destination
) VALUES
      (1, 1, 500, 'STOCK'),
      (1, 2, 1000, 'STOCK');