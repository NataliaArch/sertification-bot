require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Начнём.
        go!: /UserCheck

    state: Hello
        intent!: /привет
        a: Привет привет
        go!: /UserCheck

    state: Bye
        intent!: /пока
        a: Пока пока

    state: NoMatch
        event!: noMatch
        a: Извините, я не понял! Давайте повторю. {{$session.lastresponse}}
        event: noMatch || toState = "/NoMatch2"

    state: Match
        event!: match
        a: {{$context.intent.answer}}
        
        
    state: UserCheck
        event: noMatch || toState = "./"
        go!: /FirstEnter
        
        
    state: FirstEnter
        a: Здравствуйте. Меня зовут Глобусик, я бот тур-агентства Just Tour! Вы знали, что мир полон удивительных мест и приключений? Давайте отправимся вместе в путешествие и откроем для вас новые горизонты! Если у вас возникнут какие-то вопросы, просто скажите "Помоги"
        go!: /OnboardingA

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


    state: NoMatch2
        a: Если надо повторить, скажите "Да". А еще вы можете сказать "Помоги", и я подскажу варианты дальнейшей беседы