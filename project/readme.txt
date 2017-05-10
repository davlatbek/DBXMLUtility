For macOS:

1. Находясь в папке project, введите данную команду в командной строке mysql -uroot -p < dbtoxml.departmentBackup.sql для восстановления БД с таблицей Department
(Или полный_путь/mysql -uroot -p < полный_путь/dbtoxml.departmentBackup.sql

2. Измените настройки в файле settings.properties, введя ваш username и password

3. Введите имя файла логов в поле logName

4. Для запуска проекта и для выполнения одной из команд (extract file / sync file.xml), запустите скрипт extract.sh или sync.sh

! Если хотите изменить имя файла выгрузки, измените generated_xml_from_table в скрипте extract.sh на другое название без расширения .xml

! Если хотите изменить файл синхронизации, измените поле generated_xml_from_table.xml в скрипте sync.sh на другое название, оставив расширение .xml


For Windows:

1. Находясь в папке project, введите данную команду в командной строке mysql -uroot -p < dbtoxml.departmentBackup.sql для восстановления БД с таблицей Department
(Или полный_путь/mysql -uroot -p < полный_путь/dbtoxml.departmentBackup.sql

2. Измените настройки в файле settings.properties, введя ваш username и password

3. Введите имя файла логов в поле logName

4. Для запуска проекта и для выполнения одной из команд (extract file / sync file.xml), запустите скрипт extract.bat или sync.bat

! Если хотите изменить имя файла выгрузки, измените generated_xml_from_table в скрипте extract.bat на другое название без расширения .xml

! Если хотите изменить файл синхронизации, измените поле generated_xml_from_table.xml в скрипте sync.bat на другое название, оставив расширение .xml