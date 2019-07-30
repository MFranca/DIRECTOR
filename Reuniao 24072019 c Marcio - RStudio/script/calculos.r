library(tidyverse);
library(ggrepel);

rm(list = ls());


# =====================================================================
#
# CARGA DOS DADOS
#
# =====================================================================

profile <- tibble(
  label = c("P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10", "P11", "P12", "P13", "P14", "P15", "P16", "P17", "P18", "P19", "P20", "P21"),
  acad = c("PhD", "SP", "UG", "UG", "UG", "SP", "SP", "UG", "BS", "BS", "BS", "SP", "BS", "BS", "BS", "SP", "BS", "BS", "BS", "BS", "SP"),
  expES = c(18, 24, 1, 3, 5, 10, 18, 4, 10, 19, 4, 20, 5, 6, 10, 12, 7, 10, 12, 12, 12),
  expAS = c(8, 12, 0, 0.5, 0, 5, 10, 0.5, 6, 5, 2, 5, 1, 2, 3, 0, 1, 1, 10, 12, 6)
);

director <- tibble(
  scenario   = c("S1", "S2", "S3", "S4", "S5"),
  technical1 = c(1511, 1451,  571, 1961, 2011),
  technical2 = c(1711, 2011,  241, 1971, 2001),
  technical3 = c( 811, 1241,  301, 1871, 1781),
  social1    = c(1711, 2101,  481, 1961,  311),
  social2    = c(1511, 1451, 1421, 1971, 1641),
  social3    = c( 811, 1781,  491, 1871,  621),
  semantic1  = c(1691, 2151,  641,  831, 2151),
  semantic2  = c( 221, 1781, 2071, 1961, 1051),
  semantic3  = c(1511, 2011,  571,   71, 1251)
);

recomendation <- read_csv("~/Desktop/Marcelo Franca/data/recomendacoes.csv", col_types = cols(
  label = col_character(),
  scenario = col_character(),
  recomendation1 = col_character(),
  recomendation2 = col_character(),
  recomendation3 = col_character()
));

recomendation <- recomendation %>% 
  gather("recomendation", "value", recomendation1, recomendation2, recomendation3) %>% 
  arrange(label, scenario) %>%
  filter(!is.na(value));


# =====================================================================
#
# FASE I - SEPARACAO DOS PERFIS ENTRE JUNIOR E SENIOR
#
# =====================================================================

#
# A correlacao entre experiência em ES e experiência em AS é alta: 0.73
# isto significa que os participantes experientes em ES em geral tambem
# sao experientes em AS. Sendo assim, podemos pegar somente um dos dois 
# criterios. Elegemos AS em funcao de sua proximidade ao tema da Tese.
#
cor(profile$expES, profile$expAS, method="spearman");


#
# O grafico abaixo confirma o resultado da correlacao, mostrando que a
# maior parte dos participantes têm grande experiência com AS quando
# também tem experiência com ES e vice-versa.
#
ggplot(data = profile, aes(x = expES, y = expAS)) +
  geom_point(aes(shape = acad), size=3) +
  geom_text_repel(aes(label = label), size = 3, point.padding = 0.025, color = "blue") +
  labs(x = "Experiência em Engenharia de Software", y = "Experiência em arquitetura de software") +
  theme(legend.title = element_blank()) +
  theme(legend.position = "bottom");

ggsave(filename="~/Desktop/Marcelo Franca/results/ggplot - expAS x expSE.pdf", device="pdf", plot = last_plot(), width = 29, height = 21, unit = "cm");


#
# O dendograma abaixo mostra claramente a existência de dois grupos,
# sendo que o primeiro está no canto superior direito do gráfico de
# pontos acima e representa os experientes. O segundo grupo será 
# considerado como os juniores.
#
dd <- dist(profile$expAS);
hc <- hclust(dd);
plot(hc);


#
# Com base nos resultados acima, vamos indicar quem são os seniores
#
profile <- profile %>%
  mutate(tipo = if_else(label %in% c("P1", "P2", "P7", "P19", "P20"), "SR", "JR"));



# =====================================================================
#
# FASE II - COMPARACAO DE CONVERGENCIA ENTRE SENIORES
#
# =====================================================================

seniorAnswers <- recomendation %>%
  inner_join(profile, by="label") %>%
  filter(tipo == "SR") %>%
  select(label, scenario, recomendation, value) %>%
  group_by(scenario, value) %>% 
  summarize(count = n()) %>%
  arrange(scenario, -count);

#
# A tabela abaixo mostra a resposta dos seniores para cada cenario.
# Percebemos alta convergência nos cenários S3 e S5, nos quais as
# respostas mais votadas representam 66% e 75% de todas as respostas.
# Os cenários S1 e S4 apresentam convergência intermediária, sendo
# que suas respostas mais votadas representam 30% e 44% de todas as
# respostas. O cenário S2 não apresentou maioria e será descartado
# das análises futuras. [JUSTIFICAR QUALITATIVAMENTE]
#
# SC    5 RESP        4 RESP        3 RESP      2 RESP                    1 RESP
# S1    -             -             1511        2081, 2111                1851, 2155, 91
# S2    -             -             -           1061, 1221, 1781, 2153    2011, 1771, 931, 1
# S3    -             571           -           -                         1081, 481
# S4    -             1961          -           2159, 661                 301
# S5    1051          1251          -           -                         1771, 1601, 791
#


# =====================================================================
#
# FASE III - COMPARACAO DAS RESPOSTAS DOS SENIORS COM A FERRAMENTA
#
# =====================================================================

#
# Considerando o oráculo como as opções mais votadas pelos sêniores, temos:
#
#      Oráculo       Ferramenta
# S1 - 1511          1o técnico, 2o social, 3o semântico
# S3 - 571           1o técnico, ---------, 3o semântico
# S4 - 1961          1o técnico, 1o social, 2o semântico
# S5 - 1051 e 1251   ----------, ---------, 2o e 3o semântico
#
#
# Responder qualitativamente:
#
# * porque a perspectiva técnica falhou no cenário 5? 
#   2011, 2001 e 1781 seriam respostas válidas?
#
#
# Indicativos interessantes:
#
# * a primeira resposta técnica já é útil, mas você precisa
#   de duas ou três respostas sociais e semânticas
#
#   enriquecer a resposta com uma discussão de antiguidade
#   das ferramentas (ex. ANT e MAVEN) e diferentes volumes
#   de discussão sobre elas. Tem que considerar mais do que
#   uma resposta em função disso. Analisar um caso entre
#   os seus cenários.
#


# =====================================================================
#
# FASE IV - COMPARACAO DAS RESPOSTAS DA FERRAMENTA COM OS JUNIORS
#
# =====================================================================

# vamos ver isto em uma próxima reunião



