package com.example.pulent;

import com.example.pulent.models.Song;

import org.junit.Assert;
import org.junit.Test;

public class SongUnitTest {

    @Test
    public void song_create(){
        Song song = new Song(
                "track",
                "song",
                112018,
                1440858699,
                1440859361,
                "Nirvana",
                "In Utero (20th Anniversary) [Remastered]",
                "Dumb",
                "In Utero (20th Anniversary) [Remastered]",
                "Dumb",
                "https://music.apple.com/us/artist/nirvana/112018?uo=4",
                "https://music.apple.com/us/album/dumb/1440858699?i=1440859361&uo=4",
                "https://music.apple.com/us/album/dumb/1440858699?i=1440859361&uo=4",
                "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview128/v4/1d/4a/47/1d4a473d-0e1a-8d0c-7262-ee077ecf06d0/mzaf_5826339530962154107.plus.aac.p.m4a",
                "https://is5-ssl.mzstatic.com/image/thumb/Music128/v4/ce/2a/e6/ce2ae6a7-d38c-0c95-7f81-2e958c27daa4/source/30x30bb.jpg",
                "https://is5-ssl.mzstatic.com/image/thumb/Music128/v4/ce/2a/e6/ce2ae6a7-d38c-0c95-7f81-2e958c27daa4/source/60x60bb.jpg",
                "https://is5-ssl.mzstatic.com/image/thumb/Music128/v4/ce/2a/e6/ce2ae6a7-d38c-0c95-7f81-2e958c27daa4/source/100x100bb.jpg",
                9.99,
                1.29,
                2.99,
                "1993-09-21T12:00:00Z",
                "notExplicit",
                "notExplicit",
                1,
                1,
                12,
                151880,
                "USA",
                "USD",
                "Alternative",
                "TV-MA",
                "Hank nervously awaits medical test results while reflecting on the early days of his romance with Karen, which coincided with a famous grunge rocker's overdose.",
                "While Hank nervously awaits the results of his biopsy, he reflects on his relationship with Karen, and we flashback to the beginning of their stormy union - an event that coincides with the tragic overdose of a famous grunge rocker. Meanwhile, Daisy tells Charlie that she's moving out, but Charlie doesn't want to let her go."
                );

        Assert.assertEquals(1440859361, song.getTrackId());
        Assert.assertEquals(1440858699, song.getCollectionId());
    }
}
