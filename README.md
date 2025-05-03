# 📋 carDO - Gerenciador de Boards Kanban em Java

Cardo é um sistema de gerenciamento de quadros no estilo **Kanban**, onde você pode criar boards, adicionar cards, mover cards entre colunas (Ex: A Fazer, Fazendo, Finalizado), bloquear e desbloquear cards, e visualizar detalhes.

Este projeto foi desenvolvido utilizando **Java**, com arquitetura baseada em **camadas (UI, Service, DAO, Persistence)** e persistência de dados via **JDBC com banco de dados relacional**.

---

## 📌 Funcionalidades

* 📁 Criação de **boards** com colunas personalizadas
* 📝 Criação de **cards** com título e descrição
* 🔁 **Movimentação** de cards entre colunas
* 🚫 **Bloqueio** e 🔓 **desbloqueio** de cards com motivo
* 🗑️ **Remoção** de cards
* 🔍 Visualização de **detalhes completos** do card
* 💥 Interface em modo texto (CLI)

---

## 🛠️ Tecnologias Utilizadas

* Java 17+
* JDBC (Java Database Connectivity)
* PostgreSQL / MySQL (ou outro banco relacional via JDBC)
* Padrão DAO
* Lombok
* Camada de serviço para regras de negócio

---

## 🧱 Estrutura do Projeto

```
cardo/
├── br.com.cardo.ui
│   └── BoardMenu.java
├── br.com.cardo.service
│   └── CardService.java, BlockService.java, etc.
├── br.com.cardo.persistence.dao
│   └── CardDAO.java, BlockDAO.java, etc.
├── br.com.cardo.persistence.entity
│   └── CardEntity.java, BoardEntity.java, etc.
├── br.com.cardo.persistence.config
│   └── ConnectionConfig.java
```

---

## 🚀 Como Executar o Projeto

1. Clone o repositório:

   ```bash
   git clone https://github.com/Gabriel-Farias-06/carDO.git
   cd carDO
   ```

2. Configure o banco de dados:

   * Crie um banco de dados
   * Atualize as credenciais e URL no arquivo `ConnectionConfig.java`

3. Compile o projeto:

   ```bash
   javac -cp . src/**/*.java
   ```

4. Execute a classe principal:

   ```bash
   java br.com.carDO.Main
   ```

---

## ✅ Pré-requisitos

* JDK 17 ou superior
* Banco de dados relacional (PostgreSQL, MySQL ou outro via JDBC)
* Driver JDBC configurado no classpath
* IDE (opcional): IntelliJ, Eclipse, VSCode

---

## 💡 Melhorias Futuras

* Interface gráfica (Swing/JavaFX)
* Exportação de boards para PDF/Excel
* Integração com APIs externas
* Autenticação de usuários

---

## 👨‍💼 Autor

Desenvolvido por **Gabriel Farias**<br/><br/>
[![Email](https://img.shields.io/badge/Email-%23E4405F?style=for-the-badge)](mailto:gabrielrgfaria@gmail.com)<br/>
[![LinkedIn](https://img.shields.io/badge/LinkedIn-3670A0?style=for-the-badge)](https://www.linkedin.com/in/gabriel-do-rego-farias-138378322/)
---

