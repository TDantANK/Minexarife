#  MineXarife - Almoxarifado Inteligente baseado em mineração


---

Funcionalidades Principais

* **Gestão de Filiais:** Cadastro e monitoramento de múltiplas unidades de mineração.
* **Controle de Frotas e Ativos:** Gerenciamento de veículos e ferramentas com rastreio de responsabilidade.
* **Vínculo Inteligente:** Sistema de trava que impede o vínculo de ativos a funcionários de filiais incorretas.
* **Interface Cloud-Connected:** Sincronização instantânea via API REST com banco de dados remoto.
* **Filtro Autocomplete:** Busca inteligente em listas suspensas para otimização de tempo no cadastro.

---

Tecnologias Utilizadas

* **Linguagem:** Java 17
* **Interface Gráfica:** JavaFX
* **Persistência de Dados:** Supabase (PostgreSQL)
* **Comunicação API:** HttpClient & JSON (Jackson Databind)
* **Distribuição:** Launch4j (Geração de Executável Nativo)

---

Como Executar

Você pode testar a aplicação sem precisar instalar o Java em sua máquina:

1. Acesse a aba deste repositório.

> **Nota:** É necessária conexão com a internet para que o sistema se comunique com o banco de dados em nuvem.

---

Arquitetura do Sistema

O projeto segue o padrão de camadas para garantir manutenibilidade e escalabilidade:

1.  **Model:** Classes POJO representando as entidades do negócio.
2.  **Service:** Lógica de comunicação HTTP e persistência remota.
3.  **UI (View):** Telas desenvolvidas em JavaFX focadas em UX industrial.

---

