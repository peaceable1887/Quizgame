<!--   
    Version: 3.2.41
    Auhtor: Felix Hansmann
    
    Die Komponente "Header.vue" beinhaltet die Form und Darstellung des Header-Elementes.
-->
<template>
    <div class="container">
        <span class="headline">Quizduell</span>
    </div>
</template>

<script>
export default 
{
    name: "HeaderItem",
}
</script>

<style scoped>
.container
{
    display: flex;
    justify-content: center;
    background-color: #184e98;
    height: 50px;
}
.headline
{
    display: flex;
    align-items: center;
    padding: 0 40px 0 40px;    
    color: white;
    background-color: #184e98;
    font-size: 30px;
    font-weight: bold;
}
</style>