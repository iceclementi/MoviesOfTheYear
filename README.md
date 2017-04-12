<style type="text/css">
.image-right {
  display: block;
  margin-left: auto;
  margin-right: auto;
  float: right;
}

.image-left {
  display: block;
  margin-left: auto;
  margin-right: auto;
  float: left;
}
</style>

## Qual é a desse app?

O foco principal desse aplicativo é abordar **estratégias de desenvolvimento** essenciais relacionadas ao desenvolvimento nativo em Android, assim como conceitos importantes como **engajamento**, **experiência de usuário** e boas práticas a serem adotados no **ciclo de vida** do desenvolvimento de um app com **qualidade** e **maturidade**.

&nbsp;

## Experiência do Usuário


A **UX** (Experiência do Usuário) é um conceito que aborda como o usuário se sente em cada interação com a solução, é algo que vai muito além de construção de belas interfaces. Para ter uma boa UX é preciso (no mínimo) preocupar-se com elementos de usabilidade, facilidade na interação com os componentes, tempo de resposta e feedback para as ações realizadas pelo usuário, ter assertividade quanto as funcionalidades importantes para a aplicação, conhecer os perfis de usuários e compreender a identidade visual e sensações ao qual o usuário está acostumado em seu ambiente - por exemplo, usuários mobile Android têm experências/sensações semelhantes mesmo navegando entre diferentes aplicativos de seus dispositivos, pois os aplicativos tendem a seguir a UX do Material Design, **lançada pela Google** no Google I/O 2014.

### Material Design

Esse aplicativo segue os conceitos de UX do *Material Design*, que a grosso modo é um conceito baseado em camadas de folhas de papel, com cores "chapadas" e transições com efeitos elegantes mais próximos da realidade. Muito eficiente em dispositivos mobile, mas desenvolvido também para se adaptar em resoluções variadas como em tablet´s e desktop´s.

O Material Design foi lançado inicialmente para a versão Lollipop (e posteriores), mas atualmente já é possível utilizar bibliotecas que dão suporte ao Material mesmo para versões bem mais antigas.
> Para saber um pouco mais sobre como trabalhar com o Material em dispositivos pré-Lollipop, acesse: [Android Material Design para dispositivos Pré-Lollipop](https://goo.gl/Ubb09l){:target="_blank"}

[![Firebase Analytics](./analytics_lockups_horz_light.png)](https://firebase.google.com/features/analytics/){:target="_blank"}

UX é um processo de melhoria contínua, é preciso criar as [Personas](https://brasil.uxdesign.cc/por-que-criar-personas-bc796a1ffc7e){:target="_blank"} do seu aplicativo e **entender como seus usuários interagem com ele**. Para ajudar, de maneira bem prática e ágil nesse processo evolutivo, o firebase fornece uma ferramente de análise, que possibilita termos um **Mapa de Calor** de todo o nosso aplicativo, capturando as ações do usuário automaticamente, nos ajudando a identificar quais as ações/conteúdos mais relevantes para ele, o que já pode ser um "gancho" também para trabalhar a **Retenção** dos usuários no seu aplicativo. 
> No contexto desse app, por ex., quando um usuário adicionar algum filme da Marvel aos seus favoritos, ele passa a ser "categorizado" como fã de filmes da Marvel e ser notificado quando um trailer de qualquer filme novo da Marvel for lançado e abrir novamente o aplicativo (que pode não ter sido acessado pelo usuário há algum tempo).

&nbsp;

## Mercado

Como a versão _Jelly Bean_ ainda possui uma fatia grande do [mercado](https://developer.android.com/about/dashboards/index.html){:target="_blank"} o objetivo foi fazer com que, mesmo os usuários com essa versão do Android tenham a mesma experiência que usuários com versões mais recentes, o que exigiu um esforço extra no desenvolvimento.

Outro fator importante a ser considerado no desenvolvimento de aplicativos é a **distribuição**, no Android o esforço para ter traduções em diferentes linguagens é mínima, o que possibilita, de maneira fácil, a distruição de aplicativos de forma globalizada.

> Para esse aplicativo, duas linguagens estão sendo utilizadas, _inglês_ como opção "default" e _português_

&nbsp;

[![Proguard](./proguard-snippets.png)](http://www.thiengo.com.br/proguard-android){: .image-right target="_blank"} Além de evitar que usuários maliciosos façam engenharia reversa do seu código e tenham **acesso à dados sigilosos da sua solução**, o _Proguard_ também remove todo código e outros recursos (como imagens, por ex.) não-utilizados (inclusive das bibliotecas de terceiros referenciadas no seu app). Como resultado final, você tem um aplicativo onde nenhum _hacker_ terá acesso as suas informações e terá uma arquivo de instalação (apk) extremamente leve, com **redução de até 70%** (foi o caso desse app) no tamanho final que o usuário terá que fazer o download, o que vai evitar aquele velho papo de _"Eu baixo lá em casa, no WiFi"_.
> Para um ponto de partida no estudo do _Proguard_, dois excelentes vídeos do canal _Android Performance Patterns_ ajudam muito, um sobre [remover códigos não-utilizados](https://www.youtube.com/watch?v=5frxLkO4oTM&index=17&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE){:target="_blank"} e o outro sobre como [remover recursos não utilizados](https://www.youtube.com/watch?v=HxeW6DHEDQU&index=18&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE){:target="_blank"}.

&nbsp;

## Garantindo a qualidade do aplicativo

Rotinas de testes e captura de erros devem ser elementos essenciais no ciclo de vida do desenvolvimento de um aplicativo Android, principalmente devido à grande variedade de aparelhos disponíveis no mercado, e para ajudar nos testes, alguns conceitos/ferramentas são bastante usados por times de desenvolvimento experientes:

- [**Crash Reporting**](https://www.youtube.com/watch?v=B7mlLVAkcfU){:target="_blank"} - Serviço/ferramenta que capture os erros não tratados do aplicativo e exiba os detalhes do aparelho quando o erro ocorreu, como memória disponível, tipo de conexão, modelo do dispositivo...
> 
- [**Test Lab Automatizados**](https://www.youtube.com/watch?v=4_ZEEX1x17k){:target="_blank"} - Serviço normalmente oferecido na "nuvem" que roda testes automatizados (criados pelo desenvolvedor) e/ou também testes "robôs" - onde são feitos cliques alucinados no seu aplicativo, usando dispositivos físicos de diversos modelos.

- [**Testes via acesso remoto**](https://www.browserstack.com/){:target="_blank"} - Serviço também oferecido na "nuvem" onde é possível escolher entre diversos tipos de dispositivos **físicos** e realizar testes como se estive com o aparelho em mãos.

> Outros elementos importantes são a manutenção das versões _Alfa_ e _Beta_ para homologação antes de publicar os aplicativos para a loja oficial.

&nbsp;

[![AWS Device Farm](./aws-device-farm.png)](https://aws.amazon.com/pt/device-farm/){: .image-left target="_blank"} Para os testes automatizados e os testes por acesso remoto a Amazon Web Services oferece uma suíte muito poderosa de soluções, a [AWS Device Farm](https://aws.amazon.com/pt/device-farm/){:target="_blank"}, onde você consegue testar e interagir com aplicativos Android e iOS e aplicações web em vários dispositivos ao mesmo tempo. 
> Como você só paga pelo uso na AWS, a _Device Farm_ se torna uma opção muito interessante - em comparação aos 29$/mês da _browserstack_, pois os testes automatizados são realizados em questões de minutos, e os feitos através de acesso remoto, mesmo que manuais, não demoram também, pois o foco deles é viabilizar testes para **garantir que o app funcione como se espera em dispositivos que o(s) desenvolvedor(es) ou tester(s) não possuem** quando realizaram sua bateria de testes após conclusão do desenvolvimento do app.

&nbsp;

## Estratégias para um desenvolvimento seguro e otimizado

Durante o desenvolvimento do app é preciso ficar atento quanto ao **gerencimento de memória** - quanto de memória RAM que seu app exige, quando (e como) ele aloca e libera memória, a fim de evitar alocação indevida de memória, o que pode impactar negativamente na experiência do usuário, causando **lentidão e "travamentos"** - as incômodas [ANRs](https://developer.android.com/training/articles/perf-anr.html){:target="_blank"} - aquelas mensagens "enjoativas" exibidas pelo Sistema Operacional caso o app demore para responder à uma ação do usuário.

Um app desenvolvido sem foco em fazer um bom gerenciamento de memória também por vir a causar o que no mundo do desenvolvimento chamamos de _Memory Leaks_ ( ou "Vazamentos de Memória), algo que geralmente provoca erros no aplicativo, encerrando-o abrupta e inesperadamente, e esses erros podem variar de acordo com o dispositivo e Sistema Operacional do Usuário.

[![Android Development Patters](./android-dev-pattern.png)](https://plus.google.com/collection/sLR0p){: .image-left target="_blank"}Alguns padrões de design se aplicam no mundo do desenvolvimento Android para evitar os _Memory Leaks_ indesejáveis, principalmente no uso de _Inner Classes_, _AsyncTaks_ e _Runnables_ relacionados a elementos de visualização que precisam ser atualizados após uma operação assíncrona onde se passa o _Context_ para essa família de classes, há muita informação acerca desse assunto, mas um bom começo, principalmente para **evitar erros em "runtime"** é entender como referenciar o _Context_ através de [WeakReferences](http://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html){:target="_blank"}.

&nbsp;

[![LeakCanary](./leakcanary.png)](https://medium.com/square-corner-blog/leakcanary-detect-all-memory-leaks-875ff8360745){: .image-left target="_blank"} Mesmo seguindo os mais diversos padrões e boas práticas, com a grande variedade de dispositivos Android é **difícil para o desenvolvedor prever todos os cenários** onde possa ocorrer um "vazamento de memória" e para auxiliar *e muito* nessa tarefa é recomendável o uso do [LeakCanary](https://medium.com/square-corner-blog/leakcanary-detect-all-memory-leaks-875ff8360745){:target="_blank"} - plugin que detecta automaticamente os _memory leaks_ do seu aplicativo na fase de desenvolvimento.

&nbsp;

Outro padrão que vale o estudo é o [Retained Fragments](http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html){:target="_blank"}, pois além de ajudar a evitar as _ANRs_, a  melhorar a UX, quanto ao quesito tempo de resposta, faz com que os dados assíncronos continuem sendo "carregados" mesmo que o usuário navegue para outra funcionalidade do aplicativo. 
> Para os devs: Quando é adicionada uma nova _Activity_ ao _Foreground_ da _Back Stack_, a _Activity_ anterior da pilha de activities dessa _Back Stack_ só é destruída em algumas ocasiões especiais (caso o sistema operacional precise de mais recursos, por exemplo), e quando ela não é destruída, mesmo que não esteja em _foreground_ (sendo utilizada pelo usuário) com _Retained Fragments_ é possível capturar os dados após uma resposta de uma _AsyncTask_ em um _Listener_ e, quando o usuário retornar para essa _Activity_ os dados já estarão carregados. _Legal né?_

&nbsp;

Não podemos nos esquecer do bom e velho padrão _ViewHolder_ que **deve** ser utlizado quando é necessário exibir uma lista de itens para que haja **melhor reaproveitamento de memória**. Sem utilizar esse padrão, cada item da lista aloca um espaço de memória _X_ para "montar" sua  visualização e se a lista que o usuário está navegando têm 200 itens por exemplo, o espaço total alocado será de _X_ * 200! Com esse padrão, apenas os itens que são exibidos inicialmente na tela do disposito do usuário alocam um espaço de memória para criação da visualização, ou seja, para um dispositivo onde são exibidos cinco items na tela, mesmo que tenha 200 itens, o espaço total alocado será de _X_ * 5!
> O Facebook, o WhatsApp e o SnapChat podem usar memória à vontade do celular do usário, mas o seu app não tão glamoroso não pode...Se ele ver o seu app entre os que mais consomem memória (o que é bem fácil de verificar) você pode ter menos um usuário para o seu app.

&nbsp;

## E o que mais?

Tem algumas coisas bacanas que ainda não foram feitas nesse app (_um dia quem sabe..._) mas que são fundamentais para ter um aplicativo profissional, que viabilize uma manutenção do código-fonte sem dores de cabeça e que alcance bons indíces de retenção e engajamento de usuários.

### Estratégicamente falando...

<Imagem do Adobe UX> Antes de "pôr a mão na massa", que tal prototipar? Escolher uma boa ferramenta para criação de _Wireframes_ e _Mockups_ irão ajudá-lo a criar provas de conceito do seu app antes de começar a sua implementação, e com isso você pode fazer **Testes A/B** com alguns [segmentos mapeados de usuários](http://startupsorocaba.com/tag/value-proposition-canvas/){:target="_blank"}, por exemplo, colher feedbacks e fazer melhorias na UX do seu app antes de iniciar o processo custoso ($$$) de desenvolvimento.

Traçar planos para engajamento de usuários, entender um pouco sobre taxas de rejeição (_bounce rates_) e conversão são fundamentais para o marketing do seu app - _você não quer desenvolver um app com uma performance monstruosa, que não tenha erros, mas que seja instalado apenas por amigos e familiares_. Insumo para isso já temos com a plataforma do _Firebase_ e um bom ponto da partida é entender [as principais métricas de Marketing Digital para seu aplicativo](http://resultadosdigitais.com.br/blog/metricas-de-marketing-digital-para-aplicativos-mobile/)

&nbsp;


### Técnicamente falando...

