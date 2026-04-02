O projeto conclui todos os desafios propostos utilizando Spring Boot.
Também conta com testes unitários automatizados.

Para executá-lo existem duas formas: manualmente ou utilizando Docker.

Manualmente é um pouco mais complicado, pois exige que o usuário tenha Postgres e Redis instalado localmente e precisará criar um banco de dados com o nome "tinnova-db" e então executar o comando "./mvnw spring-boot:run".

Com Docker é bem mais simples, basta ter instalado e executar o comando "docker-compose up --build".

Com a aplicação rodando, é possível acessá-la pela url "localhost:3000" e realizar um registro pela rota "/auth/register".
Para ser um usuário administrador é preciso alterar a role pela tabela "users" do banco de dados.
Esse banco pode ser acessado por softwares como o DBeaver, na porta 5432, login admin e senha admin, ou qualquer outra que tenha usado localmente.

O projeto conta também com uma documentação Swagger, que pode ser acessada através da url "localhost:3000/swagger-ui/".

Para rodar os testes unitários é preciso executar "./mvnw test".