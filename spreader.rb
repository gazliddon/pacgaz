
require 'pp'
require 'spreadsheet'

open_book = Spreadsheet.open('media/map.xls')

sheet1 = open_book.worksheet 0

h = sheet1.row(0).size
w = (sheet1.max_by {|r| r.length}).length

puts "w =#{w} h = #{h}"

sheet1.each do |row|
  row.each do |c|
   c = ' ' unless c
    putc c
    end
  puts
  end



