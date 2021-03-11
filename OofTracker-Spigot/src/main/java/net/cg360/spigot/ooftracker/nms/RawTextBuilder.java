package net.cg360.spigot.ooftracker.nms;

import java.util.ArrayList;

//TODO: Move into commons.

// Field names should use the JSON RawText names.
public class RawTextBuilder {

    protected ArrayList<RawTextBuilder> extra;

    protected String text;
    protected String color;
    protected String font;

    protected Boolean bold;
    protected Boolean italic;
    protected Boolean underlined;
    protected Boolean strikethrough;
    protected Boolean obfuscated;

    protected String insertion;

    //TODO: Add hover + click actions and add documentation.


    public RawTextBuilder() { this(null); }
    public RawTextBuilder(String text) {
        this.text = text;
        this.color = null;
        this.font = null;

        this.bold = null;
        this.italic = null;
        this.underlined = null;
        this.strikethrough = null;
        this.obfuscated = null;

        this.insertion = null;

    }

    /**
     * Adds a component to the Raw Text's "extra" tag. Creates a list
     * if one is not already present.
     * @param extraComponent the component to add.
     */
    public void append(RawTextBuilder extraComponent) {

        if(this.extra == null) {
            this.extra = new ArrayList<>();

        }

        this.extra.add(extraComponent);
    }



    public ArrayList<RawTextBuilder> getExtra() { return extra; }
    public String getText() { return text; }
    public String getColor() { return color; }
    public String getFont() { return font; }
    public Boolean isBold() { return bold; }
    public Boolean isItalic() { return italic; }
    public Boolean isUnderlined() { return underlined; }
    public Boolean isStrikethrough() { return strikethrough; }
    public Boolean isObfuscated() { return obfuscated; }
    public String getInsertion() { return insertion; }



    public void setExtra(ArrayList<RawTextBuilder> extra) { this.extra = extra; }
    public void setText(String text) { this.text = text; }
    public void setColor(String color) { this.color = color; }
    public void setFont(String font) { this.font = font; }
    public void setBold(Boolean bold) { this.bold = bold; }
    public void setItalic(Boolean italic) { this.italic = italic; }
    public void setUnderlined(Boolean underlined) { this.underlined = underlined; }
    public void setStrikethrough(Boolean strikethrough) { this.strikethrough = strikethrough; }
    public void setObfuscated(Boolean obfuscated) { this.obfuscated = obfuscated; }
    public void setInsertion(String insertion) { this.insertion = insertion; }


    /** @return a built RawText JSON string. */
    @Override
    public String toString() {
        StringBuilder jsonTextBuilder = new StringBuilder();
        // Add a comma after every finished component. It'll be sorted at the end.
        jsonTextBuilder.append("{");

        if(extra != null) {
            jsonTextBuilder.append("[");
            boolean isEmpty = true;

            for(RawTextBuilder builder: extra) {

                if(builder != null) {
                    jsonTextBuilder.append(builder.toString()).append(",");
                    isEmpty = false;
                }
            }

            if(!isEmpty) jsonTextBuilder.deleteCharAt(jsonTextBuilder.length() - 1); // Delete last comma
            jsonTextBuilder.append(",");
        }

        if(text != null) jsonTextBuilder.append(String.format("\"text\":\"%s\"", text)).append(",");
        if(color != null) jsonTextBuilder.append(String.format("\"color\":\"%s\"", color)).append(",");
        if(font != null) jsonTextBuilder.append(String.format("\"font\":\"%s\"", font)).append(",");

        if(bold != null) jsonTextBuilder.append(String.format("\"bold\":%s", bold)).append(",");
        if(italic != null) jsonTextBuilder.append(String.format("\"italic\":%s", italic)).append(",");
        if(underlined != null) jsonTextBuilder.append(String.format("\"underlined\":%s", underlined)).append(",");
        if(strikethrough != null) jsonTextBuilder.append(String.format("\"strikethrough\":%s", strikethrough)).append(",");
        if(obfuscated != null) jsonTextBuilder.append(String.format("\"bold\":%s", obfuscated)).append(",");

        if(insertion != null) jsonTextBuilder.append(String.format("\"insertion\":\"%s\"", insertion)).append(",");



        if(jsonTextBuilder.length() > 1) { // More than just the first bracket. Probably got a comma.
            jsonTextBuilder.deleteCharAt(jsonTextBuilder.length() - 1);
        }

        jsonTextBuilder.append("}");

        return jsonTextBuilder.toString();
    }
}
