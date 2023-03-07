package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user =  new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        //If the artist does not exist, first create an artist with given name
        //Create an album with given title and artist
        Artist artist = null;
        for(Artist x : artists){
            if(x.getName().equals(artistName)){
                artist = x;
                break;
            }
        }
        if(artist ==null){
            artist = createArtist(artistName);
            Album album = new Album(title);
            albums.add(album);
            List<Album> list = new ArrayList<>();
            list.add(album);
            artistAlbumMap.put(artist,list);
            return  album;
        }
        else{
            Album album = new Album(title);
            albums.add(album);
            List<Album> list =artistAlbumMap.get(artist);
            if(list==null){
                list = new ArrayList<>();
            }
            list.add(album);
            artistAlbumMap.put(artist,list);
            return album;

        }
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        //If the album does not exist in database, throw "Album does not exist" exception
        //Create and add the song to respective album
        Album album =null;
        for(Album x : albums){
            if(x.getTitle().equals(albumName)){
                album=x;
                break;
            }
        }

        if(album == null){
            throw new Exception("Album does not exist");
        }else{
            Song song = new Song(title,length);
            songs.add(song);
            if(albumSongMap.containsKey(album)){
                List<Song> list = albumSongMap.get(album);
                list.add(song);
                albumSongMap.put(album,list);
            }
            else{
                List<Song> list = new ArrayList<>();
                list.add(song);
                albumSongMap.put(album,list);
            }
            return song;
        }

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        //Create a playlist with given title and add all songs having the given length in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
        User user = null;
        for(User x : users){
            if(x.getMobile().equals(mobile)){
                user = x;
                break;
            }
        }
        if(user==null){
            throw new Exception("User does not exist");
        }
        else{
            Playlist playlist = new Playlist(title);
            playlists.add(playlist);

            List<Song> list = new ArrayList<>();
            for(Song s : songs){
                if(s.getLength()==length) list.add(s);
            }
            playlistSongMap.put(playlist,list);

            List<User> userList = new ArrayList<>();
            userList.add(user);

            playlistListenerMap.put(playlist,userList);
            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> plist = userPlaylistMap.get(user);
                plist.add(playlist);
                userPlaylistMap.put(user,plist);

            }else{
                List<Playlist> plist = new ArrayList<>();
                plist.add(playlist);
                userPlaylistMap.put(user,plist);
            }
            return playlist;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
        User user = null;
        for(User x : users){
            if(x.getMobile().equals(mobile)){
                user = x;
                break;
            }
        }
        if(user==null){
            throw new Exception("User does not exist");
        }
        else{
            Playlist playlist = new Playlist(title);
            playlists.add(playlist);

            List<Song> list = new ArrayList<>();
            for(Song s : songs){
                if(songTitles.contains(s.getTitle())) list.add(s);
            }
            playlistSongMap.put(playlist,list);

            List<User> userList = new ArrayList<>();
            userList.add(user);

            playlistListenerMap.put(playlist,userList);
            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> plist = userPlaylistMap.get(user);
                plist.add(playlist);
                userPlaylistMap.put(user,plist);

            }else{
                List<Playlist> plist = new ArrayList<>();
                plist.add(playlist);
                userPlaylistMap.put(user,plist);
            }
            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creater or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exists, throw "Playlist does not exist" exception
        // Return the playlist after updating
        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");

        Playlist playlist = null;
        for(Playlist playlist1:playlists){
            if(playlist1.getTitle()==playlistTitle){
                playlist=playlist1;
                break;
            }
        }
        if(playlist==null)
            throw new Exception("Playlist does not exist");

        if(creatorPlaylistMap.containsKey(user))
            return playlist;

        List<User> listener = playlistListenerMap.get(playlist);
        for(User user1:listener){
            if(user1==user)
                return playlist;
        }

        listener.add(user);
        playlistListenerMap.put(playlist,listener);

        List<Playlist> playlists1 = userPlaylistMap.get(user);
        if(playlists1 == null){
            playlists1 = new ArrayList<>();
        }
        playlists1.add(playlist);
        userPlaylistMap.put(user,playlists1);

        return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for(User x : users){
            if(x.getMobile().equals(mobile)){
                user = x;
                break;
            }
        }
        if(user==null){
            throw new Exception("User does not exist");
        }
        Song song = null;
        for(Song x : songs){
            if(x.getTitle().equals(songTitle)){
                song =x;
                break;
            }
        }
        if(song==null){
            throw new Exception("Song does not exist");
        }
        if(songLikeMap.containsKey(song)) {
            List<User> list = songLikeMap.get(song);
            if(list.contains(user)){
                return song;
            }
            else {
                int like = song.getLikes() + 1;
                song.setLikes(like);
                list.add(user);
                songLikeMap.put(song, list);

                Album album = null;
                for (Album x : albumSongMap.keySet()) {
                    List<Song> list1 = albumSongMap.get(x);
                    if (list1.contains(song)) {
                        album = x;
                        break;
                    }
                }
                Artist artist = null;
                for (Artist x : artistAlbumMap.keySet()) {
                    List<Album> list2 = artistAlbumMap.get(x);
                    if (list2.contains(album)) {
                        artist = x;
                        break;
                    }
                }
                int likes = artist.getLikes() + 1;
                artist.setLikes(likes);
                artists.add(artist);

                return song;
            }
        }
        else{
                int like = song.getLikes() + 1;
                song.setLikes(like);
                List<User> l = new ArrayList<>();
                l.add(user);
                songLikeMap.put(song,l);

                Album album = null;
                for(Album x : albumSongMap.keySet()){
                    List<Song> list1 = albumSongMap.get(x);
                    if(list1.contains(song)){
                        album = x;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist x:artistAlbumMap.keySet()){
                    List<Album> list2 = artistAlbumMap.get(x);
                    if(list2.contains(album)){
                        artist = x;
                        break;
                    }
                }

                int likes = artist.getLikes()+1;
                artist.setLikes(likes);
                artists.add(artist);
                return song;

            }
    }

    public String mostPopularArtist() {
        int max =0;
        Artist artist = null;
        for(Artist x : artists){
            if(x.getLikes()>=max){
                artist=x;
                max = x.getLikes();
            }
        }
        if(artist==null){
            return null;
        }
        else{
            return artist.getName();
        }
    }


    public String mostPopularSong() {
        int max =0;
        Song song= null;
        for(Song x : songLikeMap.keySet()){
            if(x.getLikes()>=max){
                song=x;
                max = x.getLikes();
            }
        }

        if(song==null){
            return null;
        }
        else{
            return song.getTitle();
        }
    }
}
