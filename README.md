# Analisador de Classes Apex
Código em Java que analisa conjunto de classes Apex (Salesforce) para extrair uma lista de classes dispensáveis

<p align = "center">Lista de Conteúdos</p>
<p align = "center">
<a href ="#sobre">Sobre</a> *
<a href ="#linguagem">Linguagem</a> *
<a href ="#funcionamento">Funcionamento</a> *
<a href ="#contexto">Contexto</a> *
<a href ="#version">Version</a> *
<a href ="#autor">Autor</a> *
</p>


# Sobre
<p>Código que analisa um conjunto de class Apex dentro de um pacote de arquivos de determinada Org.</p>
<p>Este Código foi construído sob a demanda de analisar quais class poderiam ser dispensadas.</p>
<p>os critérios estabelecidos aqui para dispensar class foram: a)class que não são referenciadas por outras class</p>
<p>o que não inclui ser referenciadas por sí mesmas ou por suas class de teste. b)class completamente comentadas</p>
<p>Os critérios para não inclusão nessa lista de dispensa são class que usam a anotação InvocableMethod, mesmo que não estejam</p>
<p>sendo referenciadas por outras class</p>

<p>O objetivo do código é trazer uma lista de dispensa de class que não estão em uso, economizando tempo na análise manual das</p></p>class</p>

# Linguagem
<p>Java</p>

# Funcionamento
<p>O Código basicamente trabalha em três funcionalidades; a) Analise de classes de acordo com os critérios estabelecidos</p>
<p>b) Retirada de trechos de código comentados. Com isso a análise das class será somente em trechos não comentados,</p>
<p>não correndo o risco de proteger classes que estejam sendo referenciadas em por outras, mas sendo em trechos comentados</p>
<p>c) Identificando as completamente comentadas e jogando-as para a lista de dispensa </p>
<p>O código foi construído e rodado na IDE Eclipse, em projeto Maven, portanto isso deve ser considerado em sua construção</p>


# Contexto
<p>O Código foi escrito diante de um determinado contexto e de regra de negócio, portanto deve ser adaptado para cada contexto</p>
<p>e nas regras de negócio de sua Organização</p>

# Version
<p>1.0</p>

# Autor
Denis P. Coelho (https://www.linkedin.com/in/denis-pontes/)






