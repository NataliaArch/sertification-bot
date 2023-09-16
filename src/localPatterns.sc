patterns:
    #начало/конец беседы
    $greeting = (прив*/прев*/здравствуй*/хай*/hi/*драст*/мир * (~ты/~твой)/~добрый (~утро/~день/~ночь/~вечер/~время суток))
    $goodbye = (гудбай/бай/bye/досвид*/до (свид*/завтра/встреч*)/покеда/пока/всего * (добр*/хорош*)/~выйти/выход*/~закончить)

    
    #универсальные наборы фраз
    $help = (*help/sos/помоги/подскажи/помочь/подсказка/подмог*)
    $canceltour = (~выйти/выход*/стоп*/~закончить/отказ*/Не надо (продолжать/оформлять)/Не хочу (продолжать/оформлять)/Стоп/~Передумать/Отмени* заявку/не хочу/не буду [продолжать/оформлять]/надоело/отстань*)
    $refuse = (не (назову/скажу/чего/хочу/нужен/сейчас)/ни (чего/~какой/как)/никак/никакой/уйди/отстань/отвяжись)
    $repeat = (Повтори* [последни* вопрос*/ответ*]/Повтори* *[что] (ты/вы) сказал*/Не расслышал*/(Можешь/Надо) повторить/Не понял*)
    $continue = (продолжить/продолжим/вернуться к (заявке/туру))
    $changetour = {Измени*/~отредактировать [мне] [заявку/тур]}
    $remindtour = {Напомни* [мне] [заявку/тур]}
    $newtour = ((Новый/Другая) заявка/Стереть/Создать новую [заявку]/создать (заново/снова))
    $createtour = ([давай*] оформи* [мне/нам/для меня] (тур/заявку/поездку))
        
    #прочее
   # $phone = $regexp<79\d{9}>
    $phone = $regexp<((8|[+]?7)[\- ]?)?\(?\d{3}\)?[\- ]?\d{1}[\- ]?\d{1}[\- ]?\d{1}[\- ]?\d{1}[\- ]?\d{1}[\- ]?\d{1}?[\- ]?\d{1}>
    $City = $entity<cities> || converter = function(parseTree) {var id = parseTree.cities[0].value; return cities[id].value;};
    $Country = $entity<countries> || converter = function(parseTree) {var id = parseTree.countries[0].value; return countries[id].value;};
    $Name = $entity<names> || converter = function(parseTree) {var id = parseTree.names[0].value; return names[id].value;};
    $Question = (какой|какая|что с|че с|че|что|как|подскажи)
    $Weather = (~погода|~прогноз)
    $comYes = ({да [конечно]}/ага/так точно/конечно/естесственно/а как же/да/даа/дааа*/дада/дадада/lf/rjytxyj/fuf/tcntccndtyyj/da/ok)
    $comNo = (нет/ytn/нету/нэту/неат/ниат/неа/ноуп/ноу/найн/нее/неее/нееее/неееее)

    