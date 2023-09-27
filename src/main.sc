require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: requirements.sc


theme: /

    state: Start
        q!: $regex</start>
        intent!: /привет
        if: $client.tourid //переменная tourid - переключатель: 1 - начата заявка на тур, 0 - оформление тура не начато
            go!: /CrushEnter
        elseif: $client.name
            go!: /RepeatEnter
        else:
            go!: /FirstEnter

        
    state: FirstEnter
        a: Здравствуйте. Меня зовут {{ $injector.botName }}, я бот тур-агентства Just Tour! 
        a: Вы знали, что мир полон удивительных мест и приключений? Давайте отправимся вместе в путешествие и откроем для вас новые горизонты!
        a: Если у вас возникнут какие-то вопросы, просто скажите "Помоги"
        go!: /OnboardingA
        
    state: RepeatEnter
        a: Здравствуйте, {{$client.name}}. Рад видеть вас снова! Я {{ $injector.botName }} - бот туристической компании Just Tour!
        go!: /OnboardingA
        
    state: CrushEnter
        if: $client.name
            a: Здравствуйте, {{$client.name}}! Рад видеть вас снова. Напомню, что меня зовут {{ $injector.botName }}, я бот туристической фирмы Just Tour! 
        else:
            a: Здравствуйте! Рад видеть вас снова. Напомню, что меня зовут {{ $injector.botName }}, я бот туристической фирмы Just Tour!
        
        if: $client.placego  
            a: Вижу, вы недооформили заявку в город {{$client.placego}}. Напомнить вам содержание вашей заявки?
        else:
            a: Вижу, вы недооформили заявку. Напомнить вам содержание вашей заявки?
         buttons:
            "Да" -> /NewDestination  
            "Нет" -> /OnboardingA
    #возможно, стоит сделать, как Сергей предложил: проверять каждый шаг в части оформление заявки, заполнена ли каждая переменная 
    # &&& как сохранить последний шаг заполнения заявки, если crush? lastState?

    state: OnboardingA
        a: Хотите узнать погоду? А может оформить тур?
        buttons:
            "Узнать погоду" -> /AskWeather
            "Оформить тур" -> /FirstNewTour


 
    state: FirstNewTour
        a: Перехожу к оформлению заявки
        a: Оформлять заявку в {country/city}?
        buttons:
            "Да" -> /PeopleNum
            "Нет" -> /NewDestination
        event: noMatch || toState = "./"


    state: Bye
        intent!: /пока
        a: Пока пока

  # Обработка непонятных запросов
 # стейт считает до 3-х попаданий в noMatch подряд, 
 # потом связывается в HR-специалистом и записывает непонятый запрос и с кем связался в гугл таблицу
 # noContext = true означает, что следующий запрос пользователя обрабатывается в контексте стейта, в котором бот находился до перехода.
 #Параметр noContext используется для стейтов, которые не должны продвигать диалог вперед
    state: NoMatch ||noContext = true
        event!: noMatch
        
        script:
            $session.docname = "";//устанавливается исходное значение переменной, поскольку связь со специалистом отсюда не связана с адаптационным тренингом
            $session.resp = "";//устанавливается исходное значение переменной, поскольку связь со специалистом отсюда не связана с адаптационным тренингом
        
            if ($session.lastState != "/NoMatch") {
                 $session.counter_err = 1;
                 $reactions.answer("Извините, я не понял! Давайте повторю. {{$session.lastresponse}}");
            } else if ($session.counter_err < 2) {
                $session.counter_err += 1
                $temp.index = $reactions.random(answers.nomatch.phrases.length)
                $reactions.answer("Если надо повторить, скажите 'Да'. А еще вы можете сказать 'Помоги', и я подскажу варианты беседы");
            #Если пользователь отвечает "Да", то Глобусик повторяет последний вопрос сценария (Шага)
            
            } else if ($session.state = "weather") {
                $reactions.transition("./WeatherTroubles");
            }
            
            else if ($session.state = "tour") {
                $reactions.transition("./TourTroubles");
            }
            
            else {
                $reactions.transition("./OtherTroubles");
            }
  
            }
        
        state: NoMatchYes
            q: Да
            a: {{$session.lastresponse}}
                
        state: NoMatchHelp
            q: $help
            go!: /Help
            
        state: WeatherTroubles
            script: 
                $reactions.answer("Извините, я не понял, что вы сказали! Посмотреть погоду где-нибудь еще?")
            buttons:
                "Да" -> /Trouble
                "Нет" -> /Trouble
            # 
        state: TourTroubles
            script: 
                $reactions.answer("Чтобы продолжить оформление заявки, скажите 'Продолжить'. Не помните свою заявку? Скажите 'Напомнить'. А если заявку надо изменить, произнесите 'Изменить'.")
            buttons:
                "Продолжить" -> /ContinueTour
                "Напомнить" -> /RemindTour
                "Изменить" -> /ChangeTour
            # 
        state: OtherTroubles
            script: 
                $reactions.random("Сожалею, но что-то пошло не так. Всего доброго!")
                $reactions.random("Сожалею, но что-то пошло не так. Буду рад услышать вас снова!")
            # 
                

    state: Match
        event!: match
        a: {{$context.intent.answer}}
    #    script:

    #        $reactions.answer("state: Match");
        
        

