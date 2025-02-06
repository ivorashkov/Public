import { Routes, Route } from 'react-router-dom';
import { Wrapper } from './components/Wrapper';
import { Header } from './components/Header';
import { Home } from './pages/Home';

function App() {
  return (
   <Wrapper>
    <>
      <Header/>
    
      <Routes>
        <Route path='/' element={<Home/>} />
      </Routes>

      {/* <Footer/> */}
    </>
   </Wrapper>
  );
}

export default App;
