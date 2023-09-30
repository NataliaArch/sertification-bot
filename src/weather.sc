theme: /Weather
    #подсказка как запрашивать погоду. Выводится только при первом обращении по алгоритму бота
    state: FirstAskWeather
        a: Назовите дату и город или страну, и я расскажу, какая погода там будет!
        go!: /Weather/AskWeather
        
    # пользователь может задать вопрос, включающий город, страну, дату из любой части диалога    
    state: AskWeather
        #вопрос из любого места о погоде в конкретном городе
        q!: * [$Question] ($Weather/$City/$Country/@duckling.date)
        #q!: * [$Question] * $City * $Weather *
        q!: * [а в] $City *
        q!: * [а в/во/на] $Country *
        q!: * [а в] @duckling.date *
        
        script:
            log("!!!!" + toPrettyString($parseTree));
            #есть ли в запросе город?
            $temp.city = $parseTree._City;
            if ($temp.city) {
                $session.place =$parseTree._City.name; 
                $session.placetype = "city";
            }
            log("!!!!CITY" + $session.place);
            #есть ли в запросе страна?
            $temp.country = $parseTree._Country;
            if ($temp.country) {
                $session.place =$parseTree._Country.name; 
                $session.placetype = "country";
            }
            
            #есть ли в запросе дата?
    #        $temp.checkdt = $parseTree.["_duckling.date"];
    #        if ($temp.checkdt) {
     #           $session.dt =$parseTree["_duckling.date"].value;
    #        }
            
    #    if: $temp.city !== $session.place //сохраненный город не тот, же, что в запросе. Например, в запросе города нет, а в сохраненных данных он есть
    #проверить дату .в то ли диапазоне (5 дней)
    
        state: CheckWeatherRequest
            if: $session.place && $session.dt
                go: /Weather/AskWeather/CheckDate 
            elseif: $session.place === ""
                a: В каком городе или в какой стране посмотреть погоду?
                go: /Weather/AskWeather
            elseif: $session.dt === ""
                a: На какую дату смотреть погоду?
                go: /Weather/AskWeather
        
     #   state: AskPlace
     #       q: * ($City|$Country) *
     #       if: $session.dt === ""
     #           a: На какую дату смотреть погоду?
     #           go: /Weather/AskWeather/AskDate
     #       else:
     #           go: /Weather/AskWeather/CheckDate
            
        state: UnkPlace
            q: $notknow
            script:
                if ($session.place === "") {
                    $temp.randcitynum = $reactions.random(answers.randomcities.phrases.length);;
                    $session.randcity = {{answers.randomcities.phrases[$temp.randcitynum]}}
                    $reactions.answer("Посмотреть погоду в городе {{$session.randcity}}?")
                }
            
            state: Yes
                q: $comYes
                script:
                   $session.place =$session.randcity; 
                   $session.placetype = "city"; 
                   $reactions.transition("/Weather/AskWeather/CheckDate")
                    
            state: No
                q: comNo
                a: Может, тогда перейдем к оформлению тура?
                
        state: CheckDate
            script:
                var timenow = $jsapi.currentTime();
                
            a: найти формулу, чтобы проверяла дни до сегодня от запрашиваемой даты, если больше 5, или в прошлом, то обнуление переменной и
            a: Я могу посмотреть погоду только на сегодня и следующие 4 дня.
            a: иначе проверка, есть ли город или страна (см шаг выше). Если есть, то переход в TempRequest
            a: иначе в AskPlace        

    
    
  
    state: TempConfirm
        a: Температура в [place] на [date] [temp] градусов по Цельсию
        if: $session.temp > 30
            script: 
                $reactions.answer("Вы действительно планируете поездку в страну с жарким климатом?");
                
        elseif: $session.temp <15
            script:
                $reactions.answer("Вы действительно планируете поездку в страну с холодным климатом?");
        else: 
            script:
                $reactions.answer("Вы действительно планируете поездку в страну с умеренным климатом?");
                
        buttons:
            "Да" -> /Tour/PeopleNum
            "Нет" -> /Weather/AskWeather