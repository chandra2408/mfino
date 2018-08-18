package io.scal.ambi.ui.home;

/**
 * Created by chandra on 06-08-2018.
 */

public class Test {

    private void test(){
        String chara = "Chandra,Mfino";
        String[] data = chara.split(",");
        if(data.length>2){
            chara = data[0]+ ", " + data[1] + " and " + (data.length-2) + " other";
        }else{
            //do nothing
        }
    }
}
