Various sources for the coding of all this


########### http://web.telia.com/~u16122508/dacta/ ##################
DACTA controller port protocal, the innner workings at bit byte level

From:Andy Carol (carol@edfua0.ctis.af.mil)
Subject:New LEGO Robotic notes 
Newsgroups:rec.toys.lego
View this article only 
Date:1994-02-01 05:45:08 PST 
 

Notes for the LEGO Computer Lab interface box.

I made a post some weeks ago with information on the protocol used
by the LEGO Computer Lab interface box as sold by LEGO Dacta.

Here are newer versions of my notes and some corrected information.

If anybody has any questions, please feel free to contact me.

My software has gone into Beta and I will upload it to the FTP
site as soon as I can.

Requirements to use my software:

Macintosh computer.
LEGO Computer Control Lab box (with serial interface)
My software
Symatantic C 6.01.   (Think C 5.0 should also work)

I would like to see my stuff run on a PC.  It would be very 
benificial if the API (Interface) stayed the same.  If you 
want to port it, please keep in touch with me and keep the
programmer interface the same.  This will allow improvements
to benifit everybody.

---------------------------------------------------------------------
Set up computer with:

9600bps, no parity, 1 stop bit
---------------------------------------------------------------------
How to start the interface box:

Send:                p\0     <- This is the letter 'p' and a null
                                character.
                                
Box will send back various copyright things.
                           
Then send:           ###Do you byte, when I knock?$$$

Box will send back:  ###Just a bit off the block!$$$

---------------------------------------------------------------------
Software should send alives (0x02) every two seconds.

They need not be sent as long as commands are being
issued at least every two seconds.  However it may be easier
to just send alives regardless of commands being sent.
---------------------------------------------------------------------
Ouptut ports on the box are labled as A - H.  They will
be refered to as output ports 0 - 7 here.

Input ports on the box are labled as 1 - 8.  They will
be refered to as input ports 0 - 7 here.
---------------------------------------------------------------------
Most commands are one or two bytes in length.  They will be given
below in binary.  Some commands are many bytes in length.

The commands listed below are the LOGO commands as described in the
Control Lab book.

All ports have three independant states:

1) Enable           on/off
2) Power level      0 - 8
3) Direction        left/right

Power level of 0 stops the output.  You can change the direction of
the motor while it is running or stopped.  You could set the power
level (speed) of a motor, then the direction, then turn it on.  Etc, etc...

Commands to be sent to box:

mmmmmmmm  = Mask of upto eight output ports.
            Least significant bit is port zero.
            Most significant bit is port seven.

ppp       = Selection of single input or output port.

nnn       = Power level (offset by 1).
            This will be one less than the actual power level.
            0 = power level 1.
            1 = power level 2.
            ...
            7 = power level 8.

<--on-->  = duration of ON in 1/10th seconds.
<--off->  = duration of OFF in 1/10th seconds.

< break > = break signal


    Single Port Commands
    --------------------
rd (reverse)  00100ppp           Reverse direction of output port.
on            00101ppp           Turn ouput port on.
off           00111ppp           Turn output port off.
setleft       01000ppp           Set direction of output port left.
setright      01001ppp           Set direction of output port right.

    MultiPort Commands
    ------------------
off           10010000 mmmmmmmm  Turn output ports off.
on            10010001 mmmmmmmm  Turn output ports on.
setpower 0    10010010 mmmmmmmm  Set output ports to power level 0.
setright      10010011 mmmmmmmm  Set direction of output ports right.
setleft       10010100 mmmmmmmm  Set direction of output ports left.
rd (reverse)  10010101 mmmmmmmm  Reverse direction of output ports.
setpower n+1  10110nnn mmmmmmmm  Set powerlevel of output ports.

flash         11101ppp <--on--> 11100ppp <--off->   <- repeat for each port.

              10110001 mmmmmmmm  Set power level?  NEEDS MORE STUDY.
              10010001 mmmmmmmm  Turn them on

alloff        10010000 < break >  Turn all ports off.

-----------------------------------------------------------------------------
Frames sent by interface box.  About 50 a second.

19 bytes

00 00         ,  all bytes total 0xFF

-----------------------------------------------------------------------------
Single port
aaaaaaaa aaxxxxxxxx          <- Most significant byte is first in byte stream

a = analog value (10 bits)
x = status value (6 bits)
----------------------------------------------------------------------------
Samples input:

Input ports 0 - 3 are simple analog sensors.
Resistance of the input is converted to a 10 bit value.

Zero ohms yield 0x000
2Meg Ohm are about max at 0x3FF

Value rises very quickly then tapers off.

This is NOT linear!

I will upload a graph and formula later.


switch
        pressed in       0B8
        released out     3FF


temp
        freeze           2F8
        98 deg F         1D3


angle sensor

         ________
         |     o|  <- view of angle device
         |______|   


Very rough!

    M = mode        00 = idle
                    01, 10, and 11 = status.
    
      idle mode (00) does not say much.
      status mode (01 etc) is sent as the wheel crosses
                1/16 rotation edges.
      
      At low speeds an 01 is recieved.  As speed increases
      10 and 11 start to arrive.
      
       01 = wheel crossed 1/16 curve  <- normal
       10 = wheel crossed 1/8 curve?  <- only when fast
       11 = wheel crossed 1/4 curve?  <- only when real fast
                    
    S = slope       0, decreasing; 1, increasing
    C = change      0, continious; 1, slope change

           Taken together, S and C, can form a two bit value to describe
           position.  The values received will be: 10 00 11 01 which
           repeat.

    D = direction   1 = CW, 0 = CCW
    

Sample values from angle sensor

     Decimal  Hex     Frame          Broken out
     Analog   Status  Count          Status
                                     -C S D  M
1    1023     0C      (051) ----     00 1 1 00
2    0816     15      (001)  -   0   01 0 1 01
3    0768     04      (061)          00 0 1 00
4    0489     05      (001)  -   1   00 0 1 01
5    0350     04      (051)          00 0 1 00
6    0407     1D      (001)  -   2   01 1 1 01
7    0521     0C      (028)          00 1 1 00
8    0856     0D      (001)  -   3   00 1 1 01
9    1023     0C      (023) ----     00 1 1 00

For the above table the wheel was slowly turned clockwise.
Note lines 2, 4, 6, and 8 are status lines (M = 01).  These
are sent for only one frame as the wheel crossed a 1/16 turn.
All rotation data may be infered from the status lines.

The other lines (1, 3, 5, 7, and 9) are the same frames sent
over and over.  For example line 3 was recieved 61 times in
a row.  It provides little data.

I have a bit more work to do with the angle sensor.

This morning I hooked an O'scope up to the angle sensor and
learned a little more of how it works.  Tonight I will try
to apply that to my decode software.

------- Andrew

#####################################################