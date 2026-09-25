# Próximos passos

## V2 — Java + JDBC + MySQL

A segunda versão do projeto foi concluída, adicionando persistência em banco de dados com MySQL e JDBC.

Nesta etapa foram implementados:

- Persistência das entidades no banco de dados.
- Fluxo de entrada de estoque através de Invoices.
- Fluxo de saída de estoque através de Requests.
- Registro das movimentações de estoque.
- Reversão de Invoices e ProductRequests.
- Transações para operações de estoque.
- Validações e exceções relacionadas às regras de negócio.

## V3 — Spring Boot + JPA/Hibernate

A próxima etapa será reconstruir o projeto utilizando Spring Boot, JPA e Hibernate.

O objetivo será utilizar a mesma base de regras de negócio da versão anterior para comparar as abordagens de persistência e desenvolvimento.

Próximas etapas:

- [ ] Reconstruir o projeto utilizando Spring Boot.
- [ ] Implementar persistência com JPA/Hibernate.
- [ ] Criar API REST.
- [ ] Implementar autenticação e autorização.
- [ ] Avaliar melhorias de arquitetura e organização.
- [ ] Dockerizar a aplicação.
