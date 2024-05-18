import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> lines = readFile("data.txt");
        List<Program> programs = parsePrograms(lines);

        Map<BroadcastTime, List<Program>> programMap = createProgramMap(programs);
        List<Program> allPrograms = getAllPrograms(programMap);

        Collections.sort(allPrograms, Comparator.comparing(Program::getChannel).thenComparing(Program::getTime));

        // Вывод всех программ в порядке возрастания канала и времени показа
        System.out.println("All programs:");
        for (Program program : allPrograms) {
            System.out.println(program);
        }

        // Программы, которые идут сейчас
        BroadcastTime currentTime = new BroadcastTime("05:20"); // Пример текущего времени
        System.out.println("\nPrograms currently airing:");
        for (Program program : allPrograms) {
            if (program.getTime().equals(currentTime)) {
                System.out.println(program);
            }
        }

        // Поиск программ по названию
        String searchName = "ПОДКАСТ.ЛАБ. Пусть не говорят, пусть читают";
        System.out.println("\nPrograms with name \"" + searchName + "\":");
        for (Program program : allPrograms) {
            if (program.getName().equalsIgnoreCase(searchName)) {
                System.out.println(program);
            }
        }

        // Программы определенного канала, которые идут сейчас
        String searchChannel = "ПОДКАСТ.ЛАБ.";
        System.out.println("\nPrograms from channel \"" + searchChannel + "\" currently airing:");
        for (Program program : allPrograms) {
            if (program.getChannel().equalsIgnoreCase(searchChannel) && program.getTime().equals(currentTime)) {
                System.out.println(program);
            }
        }

        // Программы определенного канала в промежутке времени
        BroadcastTime startTime = new BroadcastTime("05:00");
        BroadcastTime endTime = new BroadcastTime("06:00");
        System.out.println("\nPrograms from channel \"" + searchChannel + "\" between " + startTime + " and " + endTime + ":");
        for (Program program : allPrograms) {
            if (program.getChannel().equalsIgnoreCase(searchChannel) && program.getTime().between(startTime, endTime)) {
                System.out.println(program);
            }
        }

        // Сохранение отсортированных данных в файл формата .xlsx
        saveProgramsToExcel(allPrograms, "sorted_programs.xlsx");
    }

    public static List<String> readFile(String fileName) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static List<Program> parsePrograms(List<String> lines) {
        List<Program> programs = new ArrayList<>();
        String currentChannel = "";
        for (String line : lines) {
            if (line.startsWith("#")) {
                currentChannel = line.substring(1).trim();
            } else {
                String[] parts = line.split("\n");
                if (parts.length >= 2) {
                    BroadcastTime time = new BroadcastTime(parts[0].trim());
                    String name = parts[1].trim();
                    programs.add(new Program(currentChannel, time, name));
                }
            }
        }
        return programs;
    }

    public static Map<BroadcastTime, List<Program>> createProgramMap(List<Program> programs) {
        Map<BroadcastTime, List<Program>> programMap = new TreeMap<>();
        for (Program program : programs) {
            programMap.computeIfAbsent(program.getTime(), k -> new ArrayList<>()).add(program);
        }
        return programMap;
    }

    public static List<Program> getAllPrograms(Map<BroadcastTime, List<Program>> programMap) {
        List<Program> allPrograms = new ArrayList<>();
        for (List<Program> programList : programMap.values()) {
            allPrograms.addAll(programList);
        }
        return allPrograms;
    }

    public static void saveProgramsToExcel(List<Program> programs, String fileName) {
        try (Workbook workbook = new XSSFWorkbook()) { // Создание новой рабочей книги формата .xlsx
            Sheet sheet = workbook.createSheet("Programs"); // Создание нового листа в книге
            Row headerRow = sheet.createRow(0); // Создание строки для заголовков
            headerRow.createCell(0).setCellValue("Channel"); // Установка значения ячейки
            headerRow.createCell(1).setCellValue("Time");
            headerRow.createCell(2).setCellValue("Name");

            int rowNum = 1;
            for (Program program : programs) {
                Row row = sheet.createRow(rowNum++); // Создание новой строки
                row.createCell(0).setCellValue(program.getChannel()); // Заполнение ячеек значениями
                row.createCell(1).setCellValue(program.getTime().toString());
                row.createCell(2).setCellValue(program.getName());
            }

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i); // Автоматическое изменение ширины колонок
            }

            Path filePath = Paths.get(fileName);
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent()); // Создание директории, если она не существует
            }

            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut); // Запись данных в файл
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}