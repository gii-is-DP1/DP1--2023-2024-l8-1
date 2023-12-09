package org.springframework.samples.petclinic.hex;

import java.util.List;

public class Adyacencias {
/*
    public void listAdyacencias(List<Hex> lHexs){
        for(Hex h: lHexs){
            List<Hex> aux = new ArrayList<>();
            if(h.getPosition()==0){
                for(int i=1; i<4; i++){
                Hex j=lHexs.get(i);
                aux.add(j);
                    }
            }

            if(h.getPosition()==1){
                Hex j=lHexs.get(0);
                Hex k=lHexs.get(2);
                Hex l=lHexs.get(4);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==2){
                Hex j=lHexs.get(0);
                Hex k=lHexs.get(1);
                Hex l=lHexs.get(3);
                Hex m=lHexs.get(4);
                Hex n=lHexs.get(5);
                Hex o=lHexs.get(6);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==3){
                Hex j=lHexs.get(0);
                Hex k=lHexs.get(2);
                Hex l=lHexs.get(6);
                Hex m=lHexs.get(8);
                Hex n=lHexs.get(11);
                Hex o=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==4){
                Hex j=lHexs.get(1);
                Hex k=lHexs.get(2);
                Hex l=lHexs.get(5);
                Hex m=lHexs.get(14);
                Hex n=lHexs.get(15);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==5){
                Hex j=lHexs.get(2);
                Hex k=lHexs.get(4);
                Hex l=lHexs.get(6);
                Hex m=lHexs.get(14);
                Hex n=lHexs.get(17);
                Hex o=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==6){
                Hex j=lHexs.get(2);
                Hex k=lHexs.get(3);
                Hex l=lHexs.get(5);
                Hex m=lHexs.get(11);
                Hex n=lHexs.get(17);
                Hex o=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==7){
                Hex j=lHexs.get(8);
                Hex k=lHexs.get(9);
                Hex l=lHexs.get(10);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==8){
                Hex j=lHexs.get(3);
                Hex k=lHexs.get(7);
                Hex l=lHexs.get(9);
                Hex m=lHexs.get(11);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);
            }

            if(h.getPosition()==9){
                Hex j=lHexs.get(7);
                Hex k=lHexs.get(8);
                Hex l=lHexs.get(10);
                Hex m=lHexs.get(11);
                Hex n=lHexs.get(12);
                Hex o=lHexs.get(13);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==10){
                Hex j=lHexs.get(7);
                Hex k=lHexs.get(9);
                Hex l=lHexs.get(13);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==11){
                Hex j=lHexs.get(3);
                Hex k=lHexs.get(6);
                Hex l=lHexs.get(8);
                Hex m=lHexs.get(9);
                Hex n=lHexs.get(12);
                Hex o=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==12){
                Hex j=lHexs.get(9);
                Hex k=lHexs.get(11);
                Hex l=lHexs.get(13);
                Hex m=lHexs.get(22);
                Hex n=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==13){
                Hex j=lHexs.get(9);
                Hex k=lHexs.get(10);
                Hex l=lHexs.get(12);
                Hex m=lHexs.get(21);
                Hex n=lHexs.get(22);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==14){
                Hex j=lHexs.get(4);
                Hex k=lHexs.get(5);
                Hex l=lHexs.get(15);
                Hex m=lHexs.get(16);
                Hex n=lHexs.get(17);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==15){
                Hex j=lHexs.get(14);
                Hex k=lHexs.get(16);
                Hex l=lHexs.get(18);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==16){
                Hex j=lHexs.get(14);
                Hex k=lHexs.get(15);
                Hex l=lHexs.get(17);
                Hex m=lHexs.get(18);
                Hex n=lHexs.get(19);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==17){
                Hex j=lHexs.get(5);
                Hex k=lHexs.get(14);
                Hex l=lHexs.get(16);
                Hex m=lHexs.get(20);
                Hex n=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==18){
                Hex j=lHexs.get(15);
                Hex k=lHexs.get(16);
                Hex l=lHexs.get(19);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==19){
                Hex j=lHexs.get(16);
                Hex k=lHexs.get(18);
                Hex l=lHexs.get(20);
                Hex m=lHexs.get(29);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);
            }

            if(h.getPosition()==20){
                Hex j=lHexs.get(16);
                Hex k=lHexs.get(17);
                Hex l=lHexs.get(19);
                Hex m=lHexs.get(28);
                Hex n=lHexs.get(29);
                Hex o=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==21){
                Hex j=lHexs.get(13);
                Hex k=lHexs.get(22);
                Hex l=lHexs.get(23);
                Hex m=lHexs.get(24);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);
            }

            if(h.getPosition()==22){
                Hex j=lHexs.get(12);
                Hex k=lHexs.get(13);
                Hex l=lHexs.get(21);
                Hex m=lHexs.get(23);
                Hex n=lHexs.get(25);
                Hex o=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==23){
                Hex j=lHexs.get(21);
                Hex k=lHexs.get(22);
                Hex l=lHexs.get(24);
                Hex m=lHexs.get(25);
                Hex n=lHexs.get(26);
                Hex o=lHexs.get(27);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==24){
                Hex j=lHexs.get(21);
                Hex k=lHexs.get(23);
                Hex l=lHexs.get(27);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==25){
                Hex j=lHexs.get(22);
                Hex k=lHexs.get(23);
                Hex l=lHexs.get(26);
                Hex m=lHexs.get(35);
                Hex n=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==26){
                Hex j=lHexs.get(23);
                Hex k=lHexs.get(25);
                Hex l=lHexs.get(27);
                Hex m=lHexs.get(28);
                Hex n=lHexs.get(35);
                Hex o=lHexs.get(38);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==27){
                Hex j=lHexs.get(23);
                Hex k=lHexs.get(24);
                Hex l=lHexs.get(26);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==28){
                Hex j=lHexs.get(20);
                Hex k=lHexs.get(29);
                Hex l=lHexs.get(30);
                Hex m=lHexs.get(31);
                Hex n=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==29){
                Hex j=lHexs.get(19);
                Hex k=lHexs.get(20);
                Hex l=lHexs.get(28);
                Hex m=lHexs.get(30);
                Hex n=lHexs.get(32);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==30){
                Hex j=lHexs.get(28);
                Hex k=lHexs.get(29);
                Hex l=lHexs.get(31);
                Hex m=lHexs.get(32);
                Hex n=lHexs.get(33);
                Hex o=lHexs.get(34);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==31){
                Hex j=lHexs.get(28);
                Hex k=lHexs.get(30);
                Hex l=lHexs.get(34);
                Hex m=lHexs.get(36);
                Hex n=lHexs.get(39);
                Hex o=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==32){
                Hex j=lHexs.get(29);
                Hex k=lHexs.get(30);
                Hex l=lHexs.get(33);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==33){
                Hex j=lHexs.get(30);
                Hex k=lHexs.get(32);
                Hex l=lHexs.get(34);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==34){
                Hex j=lHexs.get(30);
                Hex k=lHexs.get(31);
                Hex l=lHexs.get(33);
                Hex m=lHexs.get(39);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);
            }

            if(h.getPosition()==35){
                Hex j=lHexs.get(25);
                Hex k=lHexs.get(26);
                Hex l=lHexs.get(36);
                Hex m=lHexs.get(37);
                Hex n=lHexs.get(38);
                Hex o=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==36){
                Hex j=lHexs.get(31);
                Hex k=lHexs.get(35);
                Hex l=lHexs.get(37);
                Hex m=lHexs.get(39);
                Hex n=lHexs.get(42);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==37){
                Hex j=lHexs.get(35);
                Hex k=lHexs.get(36);
                Hex l=lHexs.get(38);
                Hex m=lHexs.get(39);
                Hex n=lHexs.get(40);
                Hex o=lHexs.get(41);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);
            }

            if(h.getPosition()==38){
                Hex j=lHexs.get(26);
                Hex k=lHexs.get(35);
                Hex l=lHexs.get(37);
                Hex m=lHexs.get(41);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);
            }

            if(h.getPosition()==39){
                Hex j=lHexs.get(31);
                Hex k=lHexs.get(34);
                Hex l=lHexs.get(36);
                Hex m=lHexs.get(37);
                Hex n=lHexs.get(40);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);
            }

            if(h.getPosition()==40){
                Hex j=lHexs.get(37);
                Hex k=lHexs.get(39);
                Hex l=lHexs.get(41);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==41){
                Hex j=lHexs.get(37);
                Hex k=lHexs.get(38);
                Hex l=lHexs.get(40);
                aux.add(j);aux.add(k);aux.add(l);
            }

            if(h.getPosition()==42){
                Hex j=lHexs.get(5);
                Hex k=lHexs.get(6);
                Hex l=lHexs.get(11);
                Hex m=lHexs.get(12);
                Hex n=lHexs.get(17);
                Hex o=lHexs.get(20);
                Hex p=lHexs.get(22);
                Hex q=lHexs.get(25);
                Hex r=lHexs.get(28);
                Hex s=lHexs.get(31);
                Hex t=lHexs.get(35);
                Hex u=lHexs.get(36);
                aux.add(j);aux.add(k);aux.add(l);aux.add(m);aux.add(n);aux.add(o);aux.add(p);aux.add(q);aux.add(r);aux.add(s);aux.add(t);aux.add(u);
            }
            h.save();
    }
    }
*/
}
