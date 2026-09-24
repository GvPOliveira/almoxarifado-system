# Almoxarifado System

Sistema de gerenciamento de estoque desenvolvido em **Java**, com foco em Programação Orientada a Objetos, regras de negócio, persistência de dados e desenvolvimento backend.

O projeto foi baseado em processos observados em um ambiente real de almoxarifado, adaptados para fins de estudo e desenvolvimento. O objetivo foi modelar situações como entrada de materiais, atendimento de requisições, controle de estoque e registro de movimentações.

## Funcionalidades

O sistema permite:

- Cadastro de produtos
- Cadastro de filiais
- Controle de estoque por filial
- Controle de localização dos produtos
- Entrada de materiais através de Invoice
- Saída de materiais através de Request
- Atendimento de requisições
- Atendimento parcial de itens solicitados
- Estorno de itens atendidos
- Estorno de Invoice
- Registro do histórico de movimentações
- Identificação da origem de cada movimentação
- Controle de diferentes destinos dos produtos de uma Invoice
- Validação de quantidade solicitada e atendida
- Validação de estoque disponível
- Prevenção de processamento duplicado
- Controle de processamento e estorno de operações
- Tratamento de regras de negócio através de Exceptions específicas

## Regras de negócio

O sistema possui regras para garantir a consistência das operações:

- Um produto possui estoque independente em cada filial.
- Uma entrada de estoque é registrada através de uma Invoice.
- Uma saída de estoque é registrada através de uma Request.
- Uma movimentação de estoque gera um registro de histórico.
- Não é possível retirar uma quantidade superior ao estoque disponível.
- Não é possível atender uma quantidade superior à quantidade solicitada.
- Uma ProductRequest pode ser atendida parcialmente.
- Uma ProductRequest processada não pode ser atendida novamente.
- Um item atendido pode ser estornado individualmente.
- Uma Invoice pode ser processada novamente após seu estorno.
- Operações que envolvem múltiplas alterações no banco de dados são executadas dentro de uma transação.

## Arquitetura

O projeto utiliza uma separação de responsabilidades entre as principais camadas:

```text
Model
 └── Entidades e regras de domínio

Service
 └── Orquestração dos casos de uso
 └── Controle das transações

Repository
 └── Persistência dos dados
 └── JDBC e SQL

Database
 └── MySQL
```

### Model

Contém as entidades responsáveis por representar o domínio da aplicação e suas regras de negócio.

Principais classes:

- **Product** — representa um produto cadastrado.
- **Branch** — representa uma filial.
- **BranchProduct** — representa um produto dentro de uma filial, controlando estoque, localização e histórico de movimentações.
- **Invoice** — representa uma nota fiscal utilizada para entrada de materiais.
- **ProductInvoice** — representa um produto e sua quantidade dentro de uma Invoice.
- **Request** — representa uma requisição de materiais.
- **ProductRequest** — representa um produto solicitado em uma Request, incluindo quantidade solicitada, atendida e estado de processamento.
- **Movement** — representa uma movimentação realizada no estoque.

### Service

Responsável por orquestrar as operações do sistema e coordenar as interações entre domínio e persistência.

Entre as operações implementadas estão:

- Processamento de Invoice
- Estorno de Invoice
- Atendimento de Request
- Estorno de ProductRequest

As operações que envolvem múltiplas alterações utilizam **transações JDBC**, garantindo `commit` ou `rollback` da operação.

### Repository

Responsável pela persistência dos dados utilizando **JDBC** e **MySQL**.

Os repositories implementam operações de:

- Inserção
- Consulta
- Atualização
- Relacionamento entre entidades
- Persistência de movimentações
- Recuperação e reconstrução dos objetos de domínio

As consultas utilizam `PreparedStatement` e operações SQL com `JOIN` e `LEFT JOIN` para reconstrução dos objetos e seus relacionamentos.

## Persistência

A aplicação utiliza **MySQL** como banco de dados e **JDBC** como tecnologia de acesso aos dados.

Entre os conceitos praticados estão:

- `Connection`
- `PreparedStatement`
- `ResultSet`
- `JOIN`
- `LEFT JOIN`
- `Transações`
- `commit`
- `rollback`
- `Generated Keys`
- `Mapeamento manual dos resultados para objetos Java`

Um dos objetivos do projeto foi realizar a persistência utilizando JDBC diretamente, permitindo compreender o funcionamento da camada de acesso a dados antes da utilização de abstrações como JPA/Hibernate.

## Controle de movimentações

Cada movimentação de estoque possui informações como:

- Identificador
- Data e hora
- Quantidade
- Tipo da movimentação
- Origem da operação
- Número da origem

Os principais tipos de movimentação são:

```text
ENTRY
OUTPUT
REVERSAL
```
## Tratamento de exceções

As regras de negócio são representadas através de Exceptions específicas.

Exemplos:

- `InvalidQuantityException`
- `InsufficientStockException`
- `RequestedQuantityExceededException`
- `InvoiceAlreadyProcessedException`
- `InvoiceAlreadyRevertedException`
- `ProductRequestAlreadyProcessedException`
- `ProductRequestAlreadyRevertedException`
- `ProductNotFoundException`
- `ProductNotFoundInBranchException`
- `BranchProductNotFoundException`
- `NoReversionException`

Isso permite diferenciar os motivos pelos quais uma operação não pode ser realizada, tornando o comportamento do domínio mais explícito.

## Testes

O projeto possui testes automatizados utilizando **JUnit** para validar regras e comportamentos do domínio.

Os testes cobrem situações como:

- Validação de quantidades
- Controle de estoque
- Regras de processamento
- Regras de estorno
- Validações de entidades
- Comportamentos relacionados às regras de negócio

## Tecnologias

- **Java**
- **MySQL**
- **JDBC**
- **JUnit**
- **Git**
- **GitHub**
- **IntelliJ IDEA**

## Principais conceitos praticados

Durante o desenvolvimento foram trabalhados conceitos como:

- Programação Orientada a Objetos
- Encapsulamento
- Associação entre objetos
- Collections (`List`, `Map`)
- `HashMap`
- `ArrayList`
- Enums
- Exceptions
- Controle de estado
- Regras de negócio
- Separação de responsabilidades
- Repository Pattern
- Service Layer
- Persistência com JDBC
- SQL
- `JOIN` e `LEFT JOIN`
- Transações
- `commit` e `rollback`
- Reconstrução de objetos a partir de dados persistidos
- Imutabilidade das coleções expostas pelas entidades

## Estrutura do projeto

```text
src
├── main
│   └── java
│       └── br.com.almoxarifado
│           ├── model
│           ├── repository
│           ├── service
│           ├── exception
│           └── ...
│
└── test
    └── java
        └── br.com.almoxarifado
```
## Banco de dados

O projeto utiliza **MySQL** para persistência dos dados.

O repositório disponibiliza o script SQL necessário para preparar o banco de dados utilizado pela aplicação.

O script é responsável por:

- Criar o banco de dados
- Criar as tabelas
- Criar as chaves primárias
- Criar os relacionamentos entre as tabelas
- Criar as chaves estrangeiras
- Preparar a estrutura necessária para execução do sistema

### Configuração

Antes de executar a aplicação:

1. Instale e inicie o MySQL.
2. Execute o script SQL disponibilizado no repositório.
3. Configure as credenciais de acesso ao banco na aplicação.
4. Execute os testes ou as operações do sistema.

> Não versione credenciais reais no repositório. Utilize credenciais locais ou variáveis de ambiente para informações sensíveis.

## Próximos passos

A versão atual representa a implementação do projeto utilizando **Java, JDBC e MySQL**.

Como próxima etapa do estudo, o projeto será reconstruído utilizando **Spring Boot**, transformando o sistema em uma **API REST**.

A reconstrução permitirá comparar as responsabilidades que foram implementadas manualmente com JDBC com as abstrações oferecidas pelo ecossistema Spring.

Possíveis evoluções futuras:

- API REST com Spring Boot
- JPA/Hibernate
- Tratamento global de Exceptions
- Autenticação e autorização
- Docker
- Documentação da API
- Novas regras e funcionalidades de negócio