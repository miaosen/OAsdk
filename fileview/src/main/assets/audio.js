function getMediaInfo() {
			var postions = '';
			var src = '';
			var audio = document.getElementsByTagName('audio');
			if (audio.length > 0) {
				for (var i = 0; i < audio.length; i++) {
					var url = audio[i].src;

					if (url == 'undefine' || url == '') {
						url = audio[i].getElementsByTagName('source')[0].src;
					}
					if (postions.length > 0) {
						postions = postions + ',' + audio[i].offsetTop;
						src = src + ',' + url;
					} else {
						postions = audio[i].offsetTop + '';
						src = url;
					}
				}
			}
			var json = '{\"postions\":\"' + postions + '\",\"src\":\"' + src + '\"}';
			return json;
		}
		 getMediaInfo();

