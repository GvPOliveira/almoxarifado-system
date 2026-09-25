# Modelo de Domínio

## Product

Representa um produto cadastrado no sistema.

Principais informações:

- código
- descrição

O código do produto é único e utilizado para sua identificação no sistema.

---

## Branch

Representa uma filial da empresa.

Principais informações:

- código
- nome
- produtos

Cada Branch mantém seus BranchProducts utilizando um Map, onde o código do produto é utilizado como chave.

Responsabilidades:

- Adicionar um produto à filial.
- Buscar um BranchProduct pelo código do produto.
- Gerenciar os produtos disponíveis na filial.

---

## BranchProduct

Representa um produto específico dentro de uma filial.

Principais informações:

- Product
- Branch
- quantidade
- localização
- histórico de movimentações

Responsabilidades:

- Adicionar quantidade ao estoque.
- Remover quantidade do estoque.
- Registrar movimentações de estoque.
- Impedir operações inválidas no estoque.

A quantidade disponível não pode ser negativa.

---

## Movement

Representa uma movimentação de estoque.

Principais informações:

- UUID
- tipo da movimentação
- tipo de origem
- número da origem
- data e hora
- quantidade
- descrição

As movimentações são registradas quando ocorre uma alteração no estoque.

Elas permitem manter o histórico das entradas, saídas e reversões realizadas.

---

## MovementType

Define os tipos disponíveis de movimentação de estoque:

- ENTRY
- OUTPUT
- REVERSAL

---

## Invoice

Representa uma nota fiscal de entrada de produtos.

Principais informações:

- número da nota fiscal
- data
- Branch de destino
- produtos
- status de processamento
- status de reversão

Uma Invoice pode conter vários ProductInvoices.

Responsabilidades:

- Validar seu processamento.
- Processar os produtos destinados ao estoque.
- Permitir a reversão de uma entrada já processada.

---

## ProductInvoice

Representa um produto dentro de uma Invoice.

Principais informações:

- Product
- quantidade
- destino

O destino define como o produto será tratado durante o processamento da Invoice:

- `STOCK`: produto destinado ao estoque da filial.
- `DIRECT`: produto destinado diretamente à operação, sem alteração no estoque.

Produtos destinados ao estoque geram uma movimentação do tipo `ENTRY`.

---

## Request

Representa uma requisição de materiais realizada por uma filial.

Principais informações:

- número da requisição
- Branch
- produtos solicitados

Uma Request pode conter vários ProductRequests.

Responsabilidades:

- Agrupar os produtos solicitados.
- Realizar o atendimento dos produtos.
- Permitir a reversão individual de produtos já atendidos.

---

## ProductRequest

Representa um produto dentro de uma Request.

Principais informações:

- BranchProduct
- quantidade solicitada
- quantidade atendida
- status de processamento
- status de reversão

A quantidade atendida não pode ser maior que a quantidade solicitada.

O atendimento pode ser parcial. Nesse caso, a quantidade restante pode ser atendida posteriormente através de outra Request.

Durante o atendimento:

- a quantidade atendida é retirada do estoque;
- uma movimentação `OUTPUT` é registrada;
- o atendimento é marcado como processado.

Uma ProductRequest processada pode posteriormente ser revertida, adicionando novamente ao estoque a quantidade que havia sido atendida.
