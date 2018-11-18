import requests
from bs4 import BeautifulSoup as bs
from PIL import Image
from io import BytesIO
import re
import eyed3


def download_song(track_url,track_name):
    track_page=requests.get(track_url)
    track_page=bs(track_page.text,'html.parser')
    p = track_page.find('p', {'style': 'text-align: center;'})
    if p is None:
        print('Error: Selected link is a video and not mp3')
        return None
    url=p.find('a')['href']
    song = requests.get(url)
    filename=track_name+'.mp3'
    filename=re.sub('[^\w\-_\. ]', '_', filename)
    with open(filename, 'wb') as file:
        file.write(song.content)
        return file.name


if __name__ == '__main__':
    url='http://songslover.live'
    search=input('Enter search term: ')
    search_page=requests.get(url, params={'s':search})
    search_page=bs(search_page.text, 'html.parser')
    articles=search_page.findAll('article', {'class': 'item-list'})
    for article in articles:
        a=article.find('h2').find('a')
        track_name=a.string
        track_url=a['href']
        icon_url=article.find('img')['src']
        print(track_name)
        icon=requests.get(icon_url).content
        Image.open(BytesIO(icon)).show()
        yn=input('Download this song? y/n: ')
        if yn[0] == 'y':
            filename = download_song(track_url,track_name)
            if filename is None:
                # don't download, go back to search page
                continue
            # set ID3 tags
            print('filename= '+filename)
            if open(filename,'rb') is None:
                print('Extraordinary')
            audiofile=eyed3.load(filename)
            if audiofile.tag is None:
                audiofile.initTag()
            audiofile.tag.images.set(3,icon,'image/jpeg')
            print('Default track name is '+track_name)
            print('Just hit enter to keep this name')
            x=input('Enter new track name: ')
            if x!='':
                track_name=x
            audiofile.tag.save()
            break
        # todo check for invalid inputs
