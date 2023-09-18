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

    state: AskWeather
        a: Назовите дату и город или страну, и я расскажу, какая погода там будет!
        a: 
        intent: /* [$Question] * {$Weather *$City *$date || toState = "/TempConfirm"
        event: noMatch || toState = "./"


 
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
            
            } else if ($session.counter_err < 4) {
                $reactions.answer("Извините, я не понял! Давайте повторю. {{$session.lastresponse}}");
                $reactions.transition("./haveTrouble");
            }
              else {
                $reactions.answer("Извините, я так и не понял вас. Подождите ответа специалиста, пожалуйста.");
                $reactions.answer("Также вы можете выбрать что-нибудь из меню внизу справа. Либо посмотрите на примеры вопросов ниже:");
                
                 //выводит 5 рандомных вариантов примеров запроса, проверяя, чтобы они не повторялись
                $temp.index1 = $reactions.random(answers.randomtopics.phrases.length);
                $temp.index2 = $reactions.random(answers.randomtopics.phrases.length);
                if ($temp.index2 == $temp.index1) {
                    $temp.index2 = "";
                }
                $temp.index3 = $reactions.random(answers.randomtopics.phrases.length);
                if ($temp.index3 == $temp.index1 || $temp.index3 == $temp.index2) {
                   $temp.index3 = "";
                }
                $temp.index4 = $reactions.random(answers.randomtopics.phrases.length);
                if ($temp.index4 == $temp.index1 || $temp.index4 == $temp.index2 || $temp.index4 == $temp.index3) {
                    $temp.index4 = "";
                }
                $temp.index5 = $reactions.random(answers.randomtopics.phrases.length);
                if ($temp.index5 == $temp.index1 || $temp.index5 == $temp.index2 || $temp.index5 == $temp.index3 || $temp.index5 == $temp.index4) {
                     $temp.index5 = "";
                }
                $reactions.answer("{{answers.randomtopics.phrases[$temp.index1]}}");
                $reactions.answer("{{answers.randomtopics.phrases[$temp.index2]}}");
                $reactions.answer("{{answers.randomtopics.phrases[$temp.index3]}}"); 
                $reactions.answer("{{answers.randomtopics.phrases[$temp.index4]}}"); 
                $reactions.answer("{{answers.randomtopics.phrases[$temp.index5]}}");
            }
            
        state: haveTrouble
            go!: /Trouble
            # 
# после третьего noMatch подряд бот готов отправить сообщение HR-специалисту о проблеме у Пользователя
    state: Trouble
   #     q!: * $hr *
        a:  Введите сообщение об интересующем вас вопросе, чтобы я передал его специалисту
              
        state: sendTrouble 
            event!: noMatch
            script:
                if ($session.docname !== "") {
                    var mes_part = " c документом '" + $session.docname + "': "
                }
                else {
                    var mes_part = ": "
                }
               
                var message = "У сотрудника " + $client.username + " есть сложности" + mes_part + $request.query
                
                if ($session.resp !== "lmanager") {
                    $temp.response = sendMessageToSmo(message, $session.resp); //вызов функции, которая передает сообещние message указаанному адресату resp
                    if ($temp.response) {
                        $reactions.answer("Ваше сообщение передано специалисту, в ближайшее время с вами свяжутся");
                    } 
                    else {
                        $reactions.answer("Какие-то помехи на линии, продублируйте позже");
                    }
                }
                else {
                    $reactions.answer("К сожалению, в этой версии я не могу напрямую связаться с вашим менеджером.");
                }
             //   $reactions.timeout({interval: 3, targetState: "/Continue"});
             
            #    $reactions.timeout({interval: 1, targetState: "/Wrong_sheet"});
                
                $session.docname = "";//устанавливается исходное значение переменной
                $session.resp = "hr"; //устанавливается исходное значение человека, которому передаются остальные сообщения
                if ($session.adapted === 0) {
                    $reactions.transition("/Adapt_sheet");
                    //$reactions.timeout({interval: 1, targetState: "/Adapt_sheet"});
                }
                else {
                    $reactions.answer("Введите ваш вопрос, а я постараюсь на него ответить.");
                }
                

    state: Match
        event!: match
        a: {{$context.intent.answer}}
    #    script:

    #        $reactions.answer("state: Match");
        
        

