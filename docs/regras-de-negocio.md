# Regras de Negócio

## Estoque

- Um produto não pode ter uma saída de estoque maior que a quantidade disponível.
- As quantidades de entrada e saída de estoque devem ser maiores que zero.
- Toda alteração de estoque deve gerar uma movimentação.
- Cada BranchProduct possui sua própria quantidade em estoque.
- Cada BranchProduct possui seu próprio histórico de movimentações.
- Um BranchProduct criado com quantidade inicial maior que zero deve registrar uma movimentação ENTRY.
- A quantidade em estoque não pode ser negativa.
- Um BranchProduct só pode existir uma vez para cada combinação de Product e Branch.

## Movimentações

- Uma movimentação deve possuir um identificador único.
- A data e hora da movimentação são registradas automaticamente.
- Os tipos de movimentação são:
  - ENTRY
  - OUTPUT
  - REVERSAL
- As quantidades das movimentações são sempre positivas.
- O tipo da movimentação determina a natureza da operação realizada no estoque.
- As movimentações de estoque são criadas pelo fluxo de entrada, saída ou reversão de estoque.
- Uma movimentação deve registrar sua origem e o número do documento que originou a operação.
- Movimentações de estoque não são criadas diretamente fora do fluxo de alteração do estoque.

## Invoices

- Uma Invoice pode conter vários produtos.
- Cada ProductInvoice define a quantidade e o destino do produto.
- Produtos destinados a STOCK devem ser adicionados ao estoque da Branch da Invoice.
- Produtos destinados a DIRECT não alteram o estoque.
- Produtos destinados a STOCK geram uma movimentação ENTRY.
- Uma Invoice só pode ser processada quando estiver em estado válido para processamento.
- Uma Invoice processada pode ser revertida.
- Uma Invoice revertida pode ser processada novamente.
- Uma Invoice não pode estar simultaneamente processada e revertida.
- A reversão de uma Invoice deve desfazer as entradas de estoque realizadas por seus produtos destinados a STOCK.

## Requisições

- Uma Request pode conter vários produtos.
- Um produto solicitado pode ser atendido parcialmente.
- A quantidade atendida não pode ser maior que a quantidade solicitada.
- A quantidade atendida não pode ser maior que a quantidade disponível em estoque.
- O atendimento de uma Request gera movimentações OUTPUT para os produtos atendidos.
- Uma ProductRequest processada não pode ser processada novamente.
- Uma ProductRequest processada pode ser revertida.
- A reversão deve devolver ao estoque a quantidade que foi efetivamente atendida.
- Uma ProductRequest não pode ser revertida antes de ser processada.
- Uma ProductRequest já revertida não pode ser revertida novamente.
- O atendimento de uma Request ocorre em uma única transação: caso alguma operação falhe, as alterações realizadas devem ser revertidas.
