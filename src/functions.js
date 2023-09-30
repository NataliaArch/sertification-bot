// установка переменных
function setVariable() {
    var session = $jsapi.context().session;
    var client = $jsapi.context().client;
    var contextPath = $jsapi.context().contextPath; //https://vk.com/video-8679781_456253651
    session.place = "";   //Номер документа из адаптационного тренинга, на котором остановился пользователь
    session.placetype = ""; //0 - Сотрудник не прошел адаптацию. 1 - Сотрудник завершил адаптацию или вначале ответил, что это не первый его рабочий день.
    session.dt = ""; //указан человек, к которому направляются все запросы, если локально не указано иное 

    
}    

//Функция для получения данных (темепратуры, ощущаемой температуры, описание погоды) по заданным координатам и на заданную дату

function getdataWeather(lat, lon, Dat) {
	var apiKey = $jsapi.context().injector.weatherApiKey;
	var response = $http.get("http://api.openweathermap.org/data/2.5/forecast?APPID=${APPID}&units=${units}&lang=${lang}&lat=${lat}&lon=${lon}", {
            timeout: 10000, //время выполнения данного запроса в милисекундах
            query:{
                APPID: apiKey,
                units: "metric",
                lang: "ru",
                lat: lat,
                lon: lon
            }
        });

	if (!response.isOk || !response.data) {
		return false;
	}

	var weather = {};

//	weather.temp = response.data.main.temp;
//	weather.feelslike = response.data.main.feels_like;
// цикл проходит по всем строкам возвращенного json объекта и сравнивает дату в переменной dt_txt из этого объекта с датой в запросе
//как только находит совпадение, сохраняет необходимые сведения о погоде на эту дату
    for (var i = 0; i < 40; i++) {
        var date = response.data.list[i].dt_txt; //вычленяе дату из запроса в формате YYYY-MM-DD HH:MM:SS
        if (date == Dat) {
            weather.dt = response.data.list[i].dt_txt;
            weather.temp = response.data.list[i].main.temp;
	        weather.feelslike = response.data.list[i].main.feels_like;
            i = 40;
        }
        
    }
    
	weather.status = response.status;

	return weather;
}

