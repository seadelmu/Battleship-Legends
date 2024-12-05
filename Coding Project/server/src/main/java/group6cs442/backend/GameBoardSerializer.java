package group6cs442.backend;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class GameBoardSerializer extends StdSerializer<GameBoard> {

    public GameBoardSerializer() {
        this(null);
    }

    public GameBoardSerializer(Class<GameBoard> t) {
        super(t);
    }

    // Gameboard needs to be serialized as JSON object with two arrays: playerBoard and serverBoard

    // This needs to happen because:
        // We need to make sure the frontend can understand the gameboard
        // We need to make sure the gameboard is serialized correctly
        // Having a JSON object with two arrays is easily understandable by the frontend

    @Override
    public void serialize(GameBoard gameBoard, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // start the object
        gen.writeStartObject();
        // write the playerBoard array
        gen.writeArrayFieldStart("playerBoard");
        // write the serverBoard array
        gen.writeArrayFieldStart("serverBoard");
        // write each row of the playerBoard
        for (String[] row : gameBoard.getPlayerBoard()) {
            gen.writeArray(row, 0, row.length);
        }

        for (String[] row : gameBoard.getServerBoard()) {
            gen.writeArray(row, 0, row.length);
        }
        // end the arrays
        gen.writeEndArray();
        gen.writeEndObject();
    }
}