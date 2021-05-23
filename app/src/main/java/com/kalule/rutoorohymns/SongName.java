package com.kalule.rutoorohymns;

public class SongName {
    private String songNumber;
    private String rutooro_songName;
    private String original_songName;


    public SongName(String songNumber, String rutooro_songName, String original_songName) {
        this.songNumber = songNumber;
        this.rutooro_songName = rutooro_songName;
        this.original_songName = original_songName;
    }

    public String getRutooroSongName() {
        return this.rutooro_songName;
    }

    public String getOriginalSongName() {
        return this.original_songName;
    }

    public String getCombinedSongName() {
        if (this.original_songName.equals("")) {        // no original title given
            return this.rutooro_songName;
        }
        else {
            return this.rutooro_songName + " - " + this.original_songName;
        }
    }

    public String getSongNumber() {
        return this.songNumber;
    }

    /**
     * Equal names -> equals songs
     * Used for contains()
     * @param object
     * @return
     */
    public boolean equals (Object object) {
        if(object==null) return false;
        if (!(object instanceof SongName))
            return false;
        if (object == this)
            return true;
        return this.songNumber.equals(((SongName) object).getSongNumber());
    }
}