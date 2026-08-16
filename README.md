# Barbershop Appointment System

Sistema completo de agendamento para barbearias desenvolvido com **Java 21 e Spring Boot**, permitindo que clientes realizem agendamentos online e que administradores gerenciem toda a agenda da barbearia.

O projeto foi desenvolvido com foco em **Clean Code, Effective Java, segurança, testes automatizados e boas práticas de arquitetura backend**.

## Funcionalidades

### Cliente

* Consulta de barbeiros disponíveis
* Consulta de dias e horários disponíveis
* Agendamento online
* Reagendamento
* Cancelamento de agendamentos
* Validação automática de disponibilidade
* Prevenção de conflitos de horário

### Administração

* Login administrativo
* Gerenciamento de barbeiros
* Gerenciamento de clientes
* Configuração da disponibilidade dos profissionais
* Visualização da agenda diária e semanal
* Criação manual de agendamentos
* Reagendamento e cancelamento
* Controle do status dos atendimentos
* Dashboard administrativo

## Tecnologias

### Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* Spring Security
* Jakarta Bean Validation
* Maven

### Banco de dados

* PostgreSQL
* Flyway

### Testes

* JUnit 5
* Mockito
* Testcontainers

### Infraestrutura

* Docker
* Git
* GitHub

## Arquitetura

O projeto utiliza uma arquitetura modular organizada por domínio:

```text
src/main/java/br/com/boostsites/barbershop/

├── auth/
├── barber/
├── customer/
├── availability/
├── appointment/
├── dashboard/
└── shared/
```

Cada domínio possui suas próprias camadas:

```text
controller
domain
dto
mapper
repository
service
```

Fluxo principal da aplicação:

```text
HTTP Request
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA / Hibernate
    ↓
PostgreSQL
```

## API

Exemplos de endpoints:

```http
POST /api/public/appointments
GET  /api/public/barbers
GET  /api/public/barbers/{id}/availability
```

Rotas administrativas:

```http
POST  /api/admin/barbers
GET   /api/admin/barbers
GET   /api/admin/customers
GET   /api/admin/appointments
PATCH /api/admin/appointments/{id}/cancel
```

As rotas administrativas são protegidas pelo **Spring Security**.

## Executando localmente

Clone o repositório:

```bash
git clone <URL_DO_REPOSITORIO>
cd barbershop
```

Configure as variáveis de ambiente necessárias para o PostgreSQL.

Depois execute:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

Também é possível executar a infraestrutura utilizando Docker.

## Testes

Execute os testes com:

```bash
./mvnw test
```

O projeto possui testes unitários e de integração para validar regras de domínio, Services, persistência e principais fluxos da aplicação.

## Princípios utilizados

* Clean Code
* Effective Java
* SOLID quando aplicável
* Baixo acoplamento
* Alta coesão
* Constructor Injection
* DTOs separados das Entities
* Proteção das invariantes do domínio
* Tratamento global de exceções
* Versionamento do banco com Flyway
* Testes automatizados
* Segurança das rotas administrativas
* Ausência de abstrações prematuras

