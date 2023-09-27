theme: /


#Куда отправляется

    state: NewDestination
        a: Куда вы хотели бы отправиться?
        intent: /$City/$Country || toState = "/PeopleNum"
        intent: /Не знаю || toState = "/UnknownDestination"
        event: noMatch || toState = "./"

    state: UnknownDestination
        a: Хорошо, я укажу это в заявке. Менеджер поможет вам выбрать место тура.
        go!: /PeopleNum


#Количество людей в поездке
    state: PeopleNum
        a: Сколько всего людей будет в поездке, включая детей?
        intent: /$number || toState = "./"
        intent: /Не знаю || toState = "./"
        event: noMatch || toState = "./"


    state: IfPeopleUnk
        a: Хорошо, я укажу, что общее число людей неизвестно.

#Количество детей в поездке
    state: KidsNum
        a: Сколько из них будет детей?
        intent: /$number || toState = "/KidsMorePeople"
        intent: /Не знаю || toState = "/IfKidsUnk"
        event: noMatch || toState = "./"

    state: IfKidsUnk
        a: Хорошо, я укажу в заявке, что число детей неизвестно.
        go!: /Budget

    state: KidsMorePeople
        if: $session.people <= $session.kids
            a: Количество детей больше общего числа людей. Скорректируйте число детей. Чтобы скорректировать общее число людей, скажите "Изменить"
            go!: /KidsNum
        else: 
            go!: /Budget

#Бюджет поездки
    state: Budget
        a: Каков общий бюджет поездки?
        intent: /$number || toState = "/TourStartDate"
        intent: /Не знаю || toState = "/BudgetUnk"
        intent: /$budgetquestions || toState = "/BudgetQuestions"
        event: noMatch || toState = "./"

    state: BudgetQuestions
        a: К сожалению, я пока не могу ответить на эти вопросы. || htmlEnabled = true, html = "К сожалению, я пока не могу ответить на эти вопросы."
        go!: /BudgetUnk

    state: BudgetUnk
        a: Указать в заявке, что бюджет неизвестен?
        buttons:
            "Да" -> /TourStartDate
            "Нет" -> /Budget
            
#Дата начала поездки
    state: TourStartDate
        a: Укажите дату начала поездки
        intent: /$duckling.date || toState = "/StartDateCheck"
        intent: /диапазон дат || toState = "/StartDateCheck"
        intent: /Не знаю || toState = "/StDateUnk"
        intent: /$StDateQuestions || toState = "/StDateQuestions"
        event: noMatch || toState = "./"

    state: StartDateCheck
        if: $session.sttourdate >= $session.today
            go!: /Duration
        else: 
            go!: /StDatePassed
            
    state: StDateQuestions
        a: К сожалению, я пока не могу ответить на эти вопросы. || htmlEnabled = true, html = "К сожалению, я пока не могу ответить на эти вопросы."
        go!: /StDateUnk

    state: StDateUnk
        a: Указать в заявке, что бюджет неизвестен?
        buttons:
            "Да" -> /Duration
            "Нет" -> /TourStartDate

    state: StDatePassed
        a: Названная дата уже прошла
        if: $temp.stdateattempt > 2
            go!: /StDateUnk
        else: 
            go!: /TourStartDate
            
#Длительность поездки
    state: Duration
        a: Укажите длительность поездки
        intent: /$number || toState = "/Stars"
        intent: /Не знаю || toState = "/DurationUnk"
        event: noMatch || toState = "./"


    state: DurationUnk
        a: Я укажу в заявке, что длительность поездки неизвестна.
        go!: /Stars

#Звездность отеля
    state: Stars
        a: Укажите желаемую звездность отеля от 1 до 5.
        intent: /$number1_5 || toState = "/UserName"
        intent: /Не знаю || toState = "/StarsUnk"
        intent: /$number6_ || toState = "/StarsWrong"
        event: noMatch || toState = "./"

    state: StarsWrong
        a: Количество звезд отеля может быть от одного до 5.
        if: $temp.starsattempt >2
            go!: /StarsUnk
        else: 
            go!: /Stars

    state: StarsUnk
        a: Я укажу в заявке, что звездность отеля неизвестна.
        go!: /UserName

#Имя пользователя
    state: UserName
        if: $client.name
            go!: /NameConfirmation
        else: 
            go!: /NewUserName

    state: NameConfirmation
        a: В заявке указать имя {{$client.name}}?
        buttons:
            "Да" -> /UserPhone
            "Нет" -> /NewUserName
        event: noMatch || toState = "./"

    state: NewUserName
        a: Укажите, пожалуйста, имя человека, на которого оформляем заявку.
        intent: /$name || toState = "/UserPhone"
        intent: /Не знаю || toState = "/NameIsObligatory"
        event: noMatch || toState = "/NameCheck"



    state: NameCheck
        a: Подтвердите, пожалуйста, что Ваше имя {{reactions.request}}.
        buttons:
            "Да" -> /UserPhone
            "Нет" -> /NewUserName

    state: NameIsObligatory
        a: Ответ на этот вопрос необходим для формирования заявки
        go!: /NewUserName
        
#Телефон пользователя        
    state: UserPhone
        if: $client.phone
            go!: /PhoneConfirmation
        else: 
            go!: /NewPhone
            
    state: PhoneConfirmation
        a: В заявке указать телефон {{$client.phone}}?
        buttons:
            "Да" -> /TourComment
            "Нет" -> /NewPhone

    state: NewPhone
        a: Укажите, пожалуйста, контактный телефон.
        intent: /$phone || toState = "/TourComment"
        intent: /Не знаю || toState = "/PhoneIsObligatory"
        event: noMatch || toState = "/PhoneIsObligatory"

    state: PhoneIsObligatory
        a: Ответ на этот вопрос необходим для формирования заявки
        go!: /PhoneIsObligatory/

#Комментарий к заявке
    state: TourComment
        a: Если вы хотите добавить что-то еще к своей заявке, скажите это. Либо произнесите "Нет", и я пропущу этот пункт.
        intent: /Нет || toState = "/TourConfirmation"
        event: noMatch || toState = "/TourConfirmation"

#Подтверждение заявки
    state: TourConfirmation
        a: "Проверьте, пожалуйста, заявку.
                Дата оформления заявки: {{$client.today}}, 
                Место поездки: {{$client.placego}}, 
                количество человек, включая детей: {{$client.people}}, 
                количество детей: {{$client.kids}},
                бюджет: {{$client.budget}},
                дата начала: {{$client.start_date}}, 
                длительность: {{$client.duration}}, 
                звездность отеля: {{$client.stars}} 
                Заявка оформлена на {{$client.name}}. 
                Телефон для связи: {{$client.phone}}, 
                Комментарии к поездке: {{$client.comment}}"
        a: Отправлять заявку менеджеру?
        buttons:
            "Да" -> /IfEmailSent
            "Нет" -> /IfTourChange

    state: IfTourChange
        a: Изменить заявку?
        buttons:
            "Да"
            "Нет" -> /TourCancel

    state: IfEmailSent
        if: response
            a: Поздравляю! Заявка отправлена. Менеджер свяжется с вами с ближайшее время!
        else: 
            a: К сожалению, возникли какие-то неполадки. Предлагаю обратиться за подбором тура в компанию по телефону 8(812)000-00-00

    state: TourCancel
        random: 
            a: Хорошо! Я сохраню вашу заявку на 24 часа. Обращайтесь, если понадоблюсь.
                Хорошего дня!
            a: В течение 2х дней вы сможете вернуться к своей заявке. Обращайтесь, если понадоблюсь.
                Хорошего вечера!
            a: Я сохраню вашу заявку на 24 часа. Надеюсь, что в следующий раз буду вам полезен! Буду рад услышать Вас снова!
            a: В течение 2х дней вы сможете вернуться к своей заявке. Приходите еще, я всегда рад Вас видеть! Хорошего вечера!


    state: ContinueTour
        a: Продолжить тур
        
    state: RemindTour
        a: Напомнить тур
        
    state: ChangeTour
        a: Изменить тур