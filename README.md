# Gestão de Projetos e Atividades

Esse é o nosso trabalho prático de Spring Boot. A ideia nasceu de um problema bem real: uma empresa de software que anota projetos em planilhas, mensagens e cadernos, e nunca sabe direito o que está pendente, o que já acabou e o que é mais urgente. A gente montou uma API que junta tudo num lugar só.

## O que ela faz

Dá para cadastrar projetos e, dentro de cada um, as atividades que precisam ser feitas. Cada atividade tem prioridade, prazo e situação (pendente, em andamento ou concluída), e sempre pertence a um projeto. Também existem equipes, com um gestor e vários desenvolvedores, e os desenvolvedores podem assumir atividades e projetos da própria equipe.

## Como a gente organizou

O código segue as camadas que vimos em aula: o **Controller** só recebe o pedido, o **Service** guarda as regras, o **Repository** conversa com o PostgreSQL e as **Entities** representam as tabelas. Nada de regra escondida nos endpoints.

## As regras que a gente levou a sério

- Nenhum campo importante pode ficar em branco.
- As datas precisam fazer sentido: nada de prazo no passado, e a atividade tem que caber no período do projeto.
- Um projeto só é concluído quando tem atividades e todas estão concluídas.
- Nada é apagado de verdade. O `setStatus` alterna projeto e atividade entre ativo e inativo a cada chamada, e não deixa desativar o que está em andamento. Foi algo falado e pensado com o professor, uma sugestão de mudança.
- Um gestor cuida de uma equipe só, e o desenvolvedor só assume o que for da equipe dele.

## Consulta especial

`GET /atividade/atrasadas` mostra o que já passou do prazo e ainda não foi concluído, com a prioridade mais alta primeiro. É a primeira coisa que um gestor quer ver na segunda-feira.

## Para rodar

É um projeto Maven com PostgreSQL (banco `trabalhoAlisson`, ajustável no `application.properties`). Na primeira vez, ele já cria umas tabelas com dados de exemplo para a gente poder testar, e o Swagger fica em `/swagger-ui.html`.

## Login e frontend

A API agora tem login com Spring Security: o usuário é o e-mail de um gestor ou desenvolvedor, com a senha dele.
Gestores podem tudo; desenvolvedores consultam e cuidam da situação das atividades. A pasta `frontend/` tem uma
interface React simples para executar todos os endpoints. Instruções em `frontend/README.md`.

## Slides

Os slides do trabalho estão na pasta `docs/`: um com a modelagem (entidades, relacionamentos, camadas e regras) e outro com a apresentação final.
