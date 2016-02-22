/*
Copyright (c) 2008 Jiri Vanek <judovana@email.cz>

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package horizontdeformer.interpolators;



public class KvadraticInterpolation implements Interpolation{
    private double a;
 private double c;
  private double b;
  private double parametr=30;

    public double getC() {
        return c;
    }

    public double getB() {
        return b;
    }

   

    public double getA() {
        return a;
    }

   
    private int x1,x2,y1,y2;
            private double x3,y3;
    /** Creates a new instance of LinearInterpolation */
    public KvadraticInterpolation(int x1,int y1,int x2, int y2,int x3,int y3){
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
       
        createInterpolation(x1,y1,x2,y2,x3,y3);
    }
    
    private void createInterpolation(int x1,int y1,int x2, int y2,int x3, int y3){
       
         this.x3=x3;
        this.y3=y3;
       a=cmpA();
       b=cmpB(a);
       c=cmpC(a,b);
        
    }
    
    public int getY(int x){
        
        return (int)((-((double)x*(double)x)-(double)a*(double)x-(double)c)/(double)b);
        
    }
    
   
  
    
    private  double cmpA(){
        double  y3 = this.y3;
        double  x3 = this.x3;
        double  y2 = this.y2;
        double  x2 = this.x2;
        double  y1 = this.y1;
        double  x1 = this.x1;
     
        return (-(x3*x3-(x1*x1))*(-y2+y1)-(x2*x2-(x1*x1))*(y3-y1))/((x3-x1)*(y2+y1)+(x2-x1)*(y3-y1));
              
    }
    private  double cmpB(double a){
        double  y3 = this.y3;
        double  x3 = this.x3;
        double  y2 = this.y2;
        double  x2 = this.x2;
        double  y1 = this.y1;
        double  x1 = this.x1;
     
        return  (x2*x2+a*x2-x1*x1-a*x1)/(y1-y2);
              //(x2^2 +a x2-x1^2 -a x1)/(y1-y2)=b
    }
    
    private  double cmpC(double a,double b){
        double  y3 = this.y3;
        double  x3 = this.x3;
        double  y2 = this.y2;
        double  x2 = this.x2;
        double  y1 = this.y1;
        double  x1 = this.x1;
        return  (-(x1*x1)-a*x1-b*y1);
              //(-x1^2 -a x1-b y1)=c
    }
    
    public static void main(String args[]) {
    KvadraticInterpolation ki=new KvadraticInterpolation(87,15,132,64,10,20);
    System.out.println(ki.getA());
    System.out.println(ki.getB());
    System.out.println(ki.getC());
    for(int x=87;x<=132;x++) System.out.println(x+" "+ ki.getY(x));   
        }

    
     
        
    }
    

/*
x2^2+ax2+by2+c=0
x3^2+ax3+by3+c=0        
----------------   (-x1^2-ax1-by1)=c
x2^2+ax2+by2-x1^2-ax1-by1=0
x3^2+ax3+by3-x1^2-ax1-by1=0
--------------- x2^2+ax2+by2-x1^2-ax1-by1=0       
--------------- x2^2+ax2-x1^2-ax1+b(-y1+y2)=0
--------------- x2^2+ax2-x1^2-ax1=-b(-y1+y2)
--------------- x2^2+ax2-x1^2-ax1=b(y1-y2)
--------------- (x2^2+ax2-x1^2-ax1)/(y1-y2)=b
a(x3-x1)+x3^2-x1^2+b(y3-y1)=0 
---------------
a(x3-x1)+x3^2-x1^2+((x2^2+ax2-x1^2-ax1)/(y1-y2))(y3-y1)=0         
a(x3-x1)(y1-y2)+(x3^2-x1^2)(y1-y2)+(x2^2+ax2-x1^2-ax1)(y3-y1)=0         
a(x3-x1)(y1-y2)+(x3^2-x1^2)(y1-y2)+(x2^2)(y3-y1)-(x1^2)(y3-y1)+a(x2-x1)(y3-y1)=0         
(x3^2-x1^2)(y1-y2)+(x2^2)(y3-y1)-(x1^2)(y3-y1)=-a(x3-x1)(y1-y2)-a(x2-x1)(y3-y1)                 
(x3^2-x1^2)(y1-y2)+(x2^2)(y3-y1)-(x1^2)(y3-y1)=a(-(x3-x1)(y1-y2)-(x2-x1)(y3-y1))                 
((x3^2-x1^2)(y1-y2)+(x2^2)(y3-y1)-(x1^2)(y3-y1))/(-(x3-x1)(y1-y2)-(x2-x1)(y3-y1))=a                 
  */      
        