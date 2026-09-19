import express from "express";
import cors from "cors";
const app=express(); app.use(cors()); app.use(express.json());
const now=()=>new Date().toISOString();

const ipos=[
 {id:"ninja-demo-1",name:"Example Technologies IPO",symbol:"EXAMPLE",type:"Mainboard",status:"OPEN",
 priceLow:700,priceHigh:735,lotSize:20,gmp:85,gmpUpdatedAt:now(),
 subscription:{qib:2.14,nii:4.62,retail:1.38,employee:3.05,overall:2.91},
 issueSize:"₹2,450 Cr",registrar:"Example Registrar",
 dates:{open:"2026-09-18",close:"2026-09-22",allotment:"2026-09-23",refund:"2026-09-24",listing:"2026-09-25"},
 description:"Demo data used only to demonstrate the IPO Ninja experience."},
 {id:"ninja-demo-2",name:"Sample Consumer IPO",symbol:"SAMPLE",type:"Mainboard",status:"UPCOMING",
 priceLow:420,priceHigh:445,lotSize:33,gmp:52,gmpUpdatedAt:now(),
 subscription:{qib:0,nii:0,retail:0,employee:0,overall:0},
 issueSize:"₹1,180 Cr",registrar:"Sample Registrar",
 dates:{open:"2026-09-24",close:"2026-09-28",allotment:"2026-09-29",refund:"2026-09-30",listing:"2026-10-01"},
 description:"Demo upcoming IPO data."}
];

app.get("/health",(_,res)=>res.json({ok:true,service:"ipo-ninja-api"}));
app.get("/api/ipos",(req,res)=>{
 const status=req.query.status?.toUpperCase();
 res.json({updatedAt:now(),data:status?ipos.filter(x=>x.status===status):ipos});
});
app.get("/api/ipos/:id",(req,res)=>{
 const x=ipos.find(x=>x.id===req.params.id);
 x?res.json({updatedAt:now(),data:x}):res.status(404).json({error:"IPO not found"});
});
app.get("/api/ipos/:id/gmp-history",(req,res)=>{
 const x=ipos.find(x=>x.id===req.params.id);
 if(!x)return res.status(404).json({error:"IPO not found"});
 res.json({data:[-25,-18,-12,-5,0].map((d,i)=>({value:x.gmp+d,date:new Date(Date.now()-(4-i)*86400000).toISOString()}))});
});
app.listen(process.env.PORT||10000,()=>console.log("IPO Ninja API running"));
