theme: /

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