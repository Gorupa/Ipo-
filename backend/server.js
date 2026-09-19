const express = require("express");
const app = express();
const PORT = process.env.PORT || 3000;
const ipos = [
  { id:"demo-tech", name:"Demo Technologies IPO", status:"open", price:"₹720–760", lot:"19 shares", gmp:"₹125", subscription:"18.4x" },
  { id:"demo-health", name:"Demo Healthcare IPO", status:"upcoming", price:"₹410–430", lot:"34 shares", gmp:"₹70", subscription:"—" }
];
app.get("/health",(req,res)=>res.json({ok:true,service:"ipo-ninja-api"}));
app.get("/api/ipos",(req,res)=>res.json({data:ipos}));
app.get("/api/ipos/:id",(req,res)=>{
  const ipo=ipos.find(x=>x.id===req.params.id);
  if(!ipo)return res.status(404).json({error:"IPO not found"});
  res.json(ipo);
});
app.listen(PORT,()=>console.log(`IPO Ninja API running on ${PORT}`));
