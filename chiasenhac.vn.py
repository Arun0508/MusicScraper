import requests
from bs4 import BeautifulSoup as bs
import sys
if __name__ == '__main__':
    page=requests.get('http://search.chiasenhac.vn/search.php',params={'s':input('Enter search term: ')})
    page=bs(page.text,'html.parser').find('table',{'class':'tbtable'}).findAll('tr')[1:]
    for item in page:
        div=item.find('div',{'class':'tenbh'})
        a=div.find('a')
        link=a['href']
        track_name=a.string
        artist=div.findAll('p')[1].string
        print(track_name+' by '+artist)
        x=input('Download this? y/n: ')
        #todo: wrong input
        if x[0]=='y':
            link = bs(requests.get(link).text, 'html.parser').find('div', {'class': 'datelast'}).findAll('a')
            if len(link)>1:
                link=link[1]['href']
            else:
                link=link[0]['href']
            page=bs(requests.get(link).text,'html.parser')
            page=page.find('div',{'id':'downloadlink2'}).findAll('a')
            quality = [0] * len(page)
            for i in range(len(page)):
                span=page[i].find('span')
                if span is not None:
                    quality[i]=page[i].find('span').string
                    print(str(i)+'. '+str(quality[i]))
                else:
                    try:
                        x=page[i].string.split()
                        for word in x:
                            if word.find('kbps')!=-1:
                                quality[i]=word
                                print(str(i)+'. '+word)
                                break
                    except AttributeError:
                        # this link is not related
                        continue
            i=int(input("Enter quality: "))
            url=page[i]['href']
            extension=url[url.rfind('.'):]
            r = requests.get(url, stream=True)
            with open(track_name+' - '+artist+' ('+str(quality[i])+')'+extension, 'wb') as f:
                total_length = int(r.headers.get('content-length'))
                downloaded=0
                print('content-length= '+str(total_length))
                for data in r.iter_content(chunk_size=int(total_length / 1000)):
                    downloaded += len(data)
                    f.write(data)
                    done = int(50 * downloaded / total_length)
                    sys.stdout.write('\r[{}{}]'.format('█' * done, '.' * (50 - done)))
                    sys.stdout.flush()
                sys.stdout.write('\n')
            break

