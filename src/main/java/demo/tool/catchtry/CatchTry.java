package demo.tool.catchtry;

public class CatchTry {
    public static void main(String[] args) {
        try {
            System.out.println("try");
            throw new RuntimeException("try");
        } catch (Exception e) {
            System.out.println("catch");
            try {
                throw new RuntimeException("catch");
            } catch (Exception e1) {
                System.out.println("catch catch");
            }
        } finally {
            System.out.println("finally");
        }
    }
}
