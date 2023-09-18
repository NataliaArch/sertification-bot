theme: /
    
    state: AskWeather
        #вопрос из любого места о погоде в конкретном городе
        q!: * [$Question] {* $Weather * $City/$Country * @duckling.date}
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
            
            #есть ли в запросе страна?
            $temp.country = $parseTree._Country;
            if ($temp.country) {
                $session.place =$parseTree._Country.name; 
                $session.placetype = "country";
            }
            
            #есть ли в запросе дата?
            $temp.checkdt = $parseTree.["_duckling.date"];
            if ($temp.checkdt) {
                $session.dt =$parseTree["_duckling.date"].value;
            }
            
    #    if: $temp.city !== $session.place //сохраненный город не тот, же, что в запросе. Например, в запросе города нет, а в сохраненных данных он есть
    #проверить дату .в то ли диапазоне (5 дней)
        if: $session.place && $session.dt
            go: /CheckDate 
        elseif: $session.place === ""
            a: В каком городе или в какой стране посмотреть погоду?
            go: /AskPlace
        elseif: $session.dt === ""
            a: На какую дату смотреть погоду?
            go: /AskDate

    state: CheckDate
        a: найти формулу, чтобы проверяла дни до сегодня от запрашиваемой даты, если больше 5, или в прошлом, то обнуление переменной и
        a: Я могу посмотреть погоду только на сегодня и следующие 4 дня.
        a: иначе проверка, есть ли город или страна (см шаг выше). Если есть, то переход в TempRequest
        a: иначе в AskPlace
    
    state: AskPlace
        q: * $City/$Country *
        q: не знаю
        if: 



    state: TempConfirm
        a: Температура в [place] на [date] [temp] градусов по Цельсию
        if: $session.temp > 30
            script:
                
        elseif: $session.temp <15
            script:
                
        else: 
            script:
                
        buttons:
            "Да" -> /PeopleNum
            "Нет" -> /AskWeather