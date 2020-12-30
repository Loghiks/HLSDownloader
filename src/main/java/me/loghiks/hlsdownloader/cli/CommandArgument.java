package me.loghiks.hlsdownloader.cli;

public class CommandArgument {

    public static final CommandArgument HELP_ARG  = new CommandArgument(

            "help",
            "",
            "Show help menu"

    );
    public static final CommandArgument FILE_ARG  = new CommandArgument(

            "file",
            "<file path>",
            "Specify the m3u8 file (Can NOT be used with the url option)"
    );
    public static final CommandArgument URL_ARG   = new CommandArgument(

            "url",
            "<url>",
            "Specify the the url that points to a m3u8 file (Can NOT be used with the file option)"
    );
    public static final CommandArgument OUT_ARG   = new CommandArgument(

            "out",
            "<file path>",
            "Specify the output file (.mp4 extension)"
    );
    public static final CommandArgument ERROR_ARG = new CommandArgument(

            "error",
            "",
            "If an error occurs, it will be printed in a file"
    );

    public static final CommandArgument[] ALL_ARGUMENTS = {HELP_ARG, FILE_ARG, URL_ARG, OUT_ARG, ERROR_ARG};

    public final String argument;
    public final String parameter;
    public final String description;

    public CommandArgument(String argument, String parameter, String description) {
        this.argument = "--" + argument;
        this.parameter = parameter;
        this.description = description;
    }

}
