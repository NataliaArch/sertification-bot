theme: /

# файл сценария с паттернами локальных переменных
require: localPatterns.sc

# запускаемые модули/библиотеки встроенных функций
require: slotfilling/slotFilling.sc
    module = sys.zb-common

# файлы с функциями на JS
require: functions.js 


# подключение внутренней болталки offTopic
#require: localofftopic.sc

# подключение внешней библиотеки для получения времени
require: dateTime/moment.min.js
    module = sys.zb-common 

# подключение словарей
#словарь для хранения текстов ответов
#require: dicts/answers.yaml
#    var = answers
#    name = answers
    
#словарь для хранения вариантов имени
require: dicts/names.csv
    var = names
    name = names

#словарь для хранения городов
require: dicts/cities.csv
    var = cities
    name = cities
    
#словарь для хранения стран
require: dicts/countries.csv
    var = countries
    name = countries
    
#словарь для хранения текстов ответов
require: dicts/answers.yaml
    var = answers
    name = answers

# файлы сценария (код разделен на модули)
# часть про погоду
require: weather.sc

# часть про заполнение заявки на тур
require: tour.sc


# стейты для отладки и тестирования
#require: score.sc

# стейты для отладки и тестирования
#require: servicezone.sc


#файл постпроцесса, который записывает информацию об ответе пользователя после каждой сессии
init:
    bind(
        "postProcess",
        function($context) {
            $context.session.lastState = $context.currentState;
            $context.session.lastAnswer = $context.response.answer;
            $context.session.lastQuery = $context.request.query;
        }
    );

    #сохранение информации по таймауту, если есть, что сохранять (то есть переменная varstoupdate=1)
    bind("postProcess", function($context) {
            $context.session.lastActiveTime = $jsapi.currentTime();
         //   $reactions.answer("{{$context.session.lastActiveTime}}");
            log($context.session.lastActiveTime)
            if ($context.session.fio !== "" && $context.session.varstoupdate === 1) {
                $reactions.timeout({interval: '1 minute', targetState: '/UpdateUserInf'});
                $context.session.varstoupdate =0;
            }
         //   else if ($context.session.varstoupdate === 1) {
         //       $reactions.timeout({interval: '1 minute', targetState: '/CheckUserId'});
         //   }
        }
    );
 