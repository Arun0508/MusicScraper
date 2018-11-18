import requests
from bs4 import BeautifulSoup as bs4
import webbrowser
import re

def display_results(links):
    choice_index = 0
    for i in range(len(links)):
        print('Result no. ' + str(i + 1))
        print("Album name: " + links[i]['title'])
        print(str(choice_index) + ": Download album")
        choice_index += 1
        print(str(choice_index) + ": Go to webpage")
        choice_index += 1
    choice = int(input('Enter the option: '))
    # todo: check for invalid choice
    link = links[choice // 2]
    if choice % 2 == 0:  # download album
        return True, 'https://starmusiq.fun' + link['href']
    else:  # go to webpage
        webbrowser.open('http://starmusiq.fun' + link['href'])
        q=input('Download this album? y/n: ')
        if q[0]=='y' or q[0]=='Y':
            return True, 'https://starmusiq.fun' + link['href']
        else:
            return False, None
if __name__ == '__main__':
    html=requests.get('https://www.google.com')
    url = 'https://www.starmusiq.fun/search/search-for-blocked-movies-starmusiq.html'
    query=input('Enter album name: ')
    search_result = requests.get(url, params={'query': query})
    soup=bs4(search_result.text,'html.parser')
    albums_container=soup.find("div", {"id": "search_albums"})
    links = albums_container.findAll('a', {'class': 'label label-danger'})
    choice=display_results(links)
    while choice[0]!=True:
        choice=display_results(links)
    url=choice[1]
    album_page=requests.get(url)
    soup=bs4(album_page.text,'html.parser')
    links=soup.findAll('a',{'style':'background:#cb413f;color:#fff;line-height:39px;padding:8px 6px;text-decoration:none;border-radius:.25em; font-weight:700;'})
    page=requests.get(links[1]['href'])
    soup=bs4(page.text,'html.parser')
    download_link_partial=soup.find('a',{'class':'btn btn-lg btn-success'})['href']
    download=requests.get('http://www.starfile.fun/download-7s-zip-new/'+download_link_partial)
    d = download.headers['content-disposition']
    fname = re.findall("filename=(.+)", d)[0]
    with open(fname[1:-1],'wb') as file:
        file.write(download.content)
