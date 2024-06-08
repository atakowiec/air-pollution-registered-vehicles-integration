package pl.pollub.is.backend.general;

public enum ExportFormat {
    JSON,
    CSV,
    XML;

    public String generateFileName() {
        return STR."data-\{System.currentTimeMillis()}.\{this.name().toLowerCase()}";
    }
}
